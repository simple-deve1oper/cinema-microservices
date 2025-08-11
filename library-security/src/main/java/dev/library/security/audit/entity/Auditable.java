package dev.library.security.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Абстрактный класс, который реализует аудит для сущностей
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    /**
     * Кто создал запись
     */
    @Column(name = "created_by", length = 100)
    @CreatedBy
    private String createdBy;
    /**
     * Когда была создана запись
     */
    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private OffsetDateTime createdDate;
    /**
     * Кто обновил запись
     */
    @Column(name = "updated_by", length = 100)
    @LastModifiedBy
    private String updatedBy;
    /**
     * Когда была обновлена запись
     */
    @Column(name = "updated_date", nullable = false)
    @LastModifiedDate
    private OffsetDateTime updatedDate;

    public Auditable() {}

    public Auditable(String createdBy, OffsetDateTime createdDate, String updatedBy, OffsetDateTime updatedDate) {
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public OffsetDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(OffsetDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Auditable auditable = (Auditable) o;
        return Objects.equals(createdBy, auditable.createdBy) && Objects.equals(createdDate, auditable.createdDate) && Objects.equals(updatedBy, auditable.updatedBy) && Objects.equals(updatedDate, auditable.updatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdBy, createdDate, updatedBy, updatedDate);
    }
}
