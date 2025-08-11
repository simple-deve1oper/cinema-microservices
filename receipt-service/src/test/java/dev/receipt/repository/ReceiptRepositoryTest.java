package dev.receipt.repository;

import dev.library.test.config.AbstractRepositoryTest;
import dev.receipt.entity.Receipt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Optional;

@ActiveProfiles("test")
public class ReceiptRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ReceiptRepository receiptRepository;

    @BeforeEach
    void setUp() {
        Receipt receiptOne = new Receipt();
        receiptOne.setBookingId(1L);
        receiptOne.setUserId("3c59a7b2-4cff-49b6-a654-3145ecdab36b");
        receiptOne.setData(new byte[]{1,2,3});
        receiptOne.setCreatedDate(OffsetDateTime.now());
        receiptOne.setUpdatedDate(OffsetDateTime.now());
        receiptRepository.save(receiptOne);

        Receipt receiptTwo = new Receipt();
        receiptTwo.setBookingId(2L);
        receiptTwo.setUserId("14b8135e-4a62-4104-ac6a-26eefaeeef17");
        receiptTwo.setData(new byte[]{4,5,6});
        receiptTwo.setCreatedDate(OffsetDateTime.now());
        receiptTwo.setUpdatedDate(OffsetDateTime.now());
        receiptRepository.save(receiptTwo);
    }

    @Test
    void existsByBookingId() {
        boolean result = receiptRepository.existsByBookingId(1L);
        Assertions.assertTrue(result);

        result = receiptRepository.existsByBookingId(999L);
        Assertions.assertFalse(result);
    }

    @Test
    void findDataByBookingId_ok() {
        Optional<byte[]> optionalBytes = receiptRepository.findDataByBookingId(2L);
        Assertions.assertTrue(optionalBytes.isPresent());

        byte[] bytes = optionalBytes.get();
        Assertions.assertArrayEquals(new byte[]{4,5,6}, bytes);
    }

    @Test
    void findDataByBookingId_empty() {
        Optional<byte[]> optionalBytes = receiptRepository.findDataByBookingId(999L);
        Assertions.assertTrue(optionalBytes.isEmpty());
    }

    @Test
    void updateDataByBookingId() {
        byte[] newData = new byte[]{7,8,9};
        receiptRepository.updateDataByBookingId(1L, newData);
        Optional<byte[]> optionalBytes = receiptRepository.findDataByBookingId(1L);
        Assertions.assertTrue(optionalBytes.isPresent());
        byte[] bytes = optionalBytes.get();
        Assertions.assertArrayEquals(newData, bytes);
    }

    @Test
    void updateUserIdDataByBookingId() {
        String userId = "3ca5d554-4102-4fa5-bc54-c355502b1fe5";
        byte[] newData = new byte[]{10,15,20};
        receiptRepository.updateUserIdAndDataByBookingId(1L, userId, newData);
        Optional<byte[]> optionalBytes = receiptRepository.findDataByBookingId(1L);
        Assertions.assertTrue(optionalBytes.isPresent());
        byte[] bytes = optionalBytes.get();
        Assertions.assertArrayEquals(newData, bytes);
    }

    @Test
    void deleteByBookingId() {
        receiptRepository.deleteByBookingId(1L);
        boolean result = receiptRepository.existsByBookingId(1L);
        Assertions.assertFalse(result);
    }
}
