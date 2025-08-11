package dev.file.image.service.impl;

import dev.file.image.entity.Image;
import dev.file.image.mapper.ImageMapper;
import dev.file.image.repository.ImageRepository;
import dev.file.image.service.FileService;
import dev.file.image.service.ImageService;
import dev.file.image.util.FileUtils;
import dev.library.core.exception.BadRequestException;
import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.file.dto.ImageRequest;
import dev.library.domain.file.dto.ImageResponse;
import dev.library.domain.file.dto.constant.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис, реализующий интерфейс {@link ImageService}
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository repository;
    private final ImageMapper mapper;
    private final FileService fileService;

    @Value("${errors.image.id.not-found}")
    private String errorsImageIdNotFound;
    @Value("${errors.image.name.already-exists}")
    private String errorsImageNameAlreadyExists;
    @Value("${errors.image.movie-id.bad-request}")
    private String errorsImageMovieIdBadRequest;
    @Value("${errors.image.movie-id-and-number.not-found}")
    private String errorsImageMovieIdAndNumberNotFound;
    @Value("${errors.image.number.bad-request}")
    private String errorsImageNumberBadRequest;
    @Value("${errors.image.number-is-ordinal.bad-request}")
    private String errorsImageNumberIsOrdinalBadRequest;

    @Override
    public List<ImageResponse> getAll() {
        List<Image> images = repository.findAll();

        return images.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<ImageResponse> getAllByMovieId(Long movieId) {
        List<Image> images = repository.findAllByMovieId(movieId);

        return images.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public Resource getResourceByMovieIdAndNumber(Long movieId, Integer number) {
        Optional<Image> optionalImage = repository.findByMovieIdAndNumber(movieId, number);
        if (optionalImage.isEmpty()) {
            String errorMessage = errorsImageMovieIdAndNumberNotFound.formatted(movieId, number);
            throw new EntityNotFoundException(errorMessage);
        }
        Image image = optionalImage.get();

        return fileService.get(image.getFileName(), FileType.IMAGE);
    }

    @Override
    @Transactional
    public void create(Long movieId, MultipartFile file) {
        FileUtils.checkExtensionImage(file);
        String fileName = file.getOriginalFilename();
        if (repository.existsByFileName(fileName)) {
            String errorMessage = errorsImageNameAlreadyExists.formatted(fileName);
            throw new EntityAlreadyExistsException(errorMessage);
        }
        fileService.save(file,FileType.IMAGE);
        int counts = repository.countByMovieId(movieId);

        Image image = Image.builder()
                .movieId(movieId)
                .fileName(fileName)
                .number(counts + 1)
                .build();
        repository.save(image);
    }

    @Override
    @Transactional
    public void updateImageNumbers(List<ImageRequest> requests) {
        Set<Long> movieIds = requests.stream().map(ImageRequest::movieId).collect(Collectors.toSet());
        if (movieIds.size() > 1) {
            throw new BadRequestException(errorsImageMovieIdBadRequest);
        }
        Set<Integer> numbers = requests.stream().map(ImageRequest::number).collect(Collectors.toSet());
        if (numbers.size() != requests.size()) {
            throw new BadRequestException(errorsImageNumberBadRequest);
        }
        if (!isOrdinalNumbers(requests)) {
            throw new BadRequestException(errorsImageNumberIsOrdinalBadRequest);
        }
        requests.forEach(request -> repository
                .editNumberByMovieId(request.movieId(), request.fileName(), request.number()));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Image image = repository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = errorsImageIdNotFound.formatted(id);
                    return new EntityNotFoundException(errorMessage);
                });
        fileService.delete(image.getFileName(), FileType.IMAGE);
        repository.deleteById(id);
    }

    /**
     * Проверка списка на то, что они объекты расположены по порядку
     * @param requests - список объектов типа {@link ImageRequest}
     */
    private boolean isOrdinalNumbers(List<ImageRequest> requests) {
        for (int i = 0; i < requests.size(); i++) {
            Integer number = i + 1;
            if (!Objects.equals(requests.get(i).number(), number)) {
                return false;
            }
        }

        return true;
    }
}
