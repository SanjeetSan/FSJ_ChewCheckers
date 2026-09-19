package com.smartnutrition.service;

import com.smartnutrition.dto.request.LunchboxPresetRequest;
import com.smartnutrition.dto.response.LunchboxPresetResponse;
import com.smartnutrition.entity.LunchboxPreset;
import com.smartnutrition.entity.Student;
import com.smartnutrition.repository.LunchboxPresetRepository;
import com.smartnutrition.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LunchboxPresetService {

    private final LunchboxPresetRepository presetRepository;
    private final StudentRepository studentRepository;

    public LunchboxPresetService(LunchboxPresetRepository presetRepository,
                                  StudentRepository studentRepository) {
        this.presetRepository = presetRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<LunchboxPresetResponse> getPresetsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        return presetRepository.findByStudentIdOrderByIsDefaultDescCreatedAtDesc(student.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public LunchboxPresetResponse createPreset(Long studentId, LunchboxPresetRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        validateDimensions(request.presetName(), request.lengthCm(), request.widthCm(), request.heightCm());

        List<LunchboxPreset> existingPresets = presetRepository.findByStudentIdOrderByIsDefaultDescCreatedAtDesc(studentId);
        boolean isFirstPreset = existingPresets.isEmpty();

        boolean setAsDefault = isFirstPreset || (request.isDefault() != null && request.isDefault());

        if (setAsDefault) {
            presetRepository.resetDefaultFlagsForStudent(studentId);
        }

        LunchboxPreset preset = LunchboxPreset.builder()
                .student(student)
                .presetName(request.presetName().trim())
                .lengthCm(request.lengthCm())
                .widthCm(request.widthCm())
                .heightCm(request.heightCm())
                .notes(request.notes() != null ? request.notes().trim() : null)
                .isDefault(setAsDefault)
                .build();

        LunchboxPreset saved = presetRepository.save(preset);

        // Also update student primary box dimensions if set as default
        if (setAsDefault) {
            updateStudentPrimaryBoxDimensions(student, saved);
        }

        return mapToResponse(saved);
    }

    @Transactional
    public LunchboxPresetResponse updatePreset(Long studentId, Long presetId, LunchboxPresetRequest request) {
        LunchboxPreset preset = presetRepository.findByIdAndStudentId(presetId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("Preset not found with ID: " + presetId + " for student: " + studentId));

        validateDimensions(request.presetName(), request.lengthCm(), request.widthCm(), request.heightCm());

        boolean setAsDefault = request.isDefault() != null && request.isDefault();
        if (setAsDefault && !preset.getIsDefault()) {
            presetRepository.resetDefaultFlagsForStudent(studentId);
            preset.setIsDefault(true);
        } else if (request.isDefault() != null) {
            preset.setIsDefault(request.isDefault());
        }

        preset.setPresetName(request.presetName().trim());
        preset.setLengthCm(request.lengthCm());
        preset.setWidthCm(request.widthCm());
        preset.setHeightCm(request.heightCm());
        preset.setNotes(request.notes() != null ? request.notes().trim() : null);

        LunchboxPreset updated = presetRepository.save(preset);

        if (Boolean.TRUE.equals(updated.getIsDefault())) {
            updateStudentPrimaryBoxDimensions(preset.getStudent(), updated);
        }

        return mapToResponse(updated);
    }

    @Transactional
    public LunchboxPresetResponse setDefaultPreset(Long studentId, Long presetId) {
        LunchboxPreset preset = presetRepository.findByIdAndStudentId(presetId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("Preset not found with ID: " + presetId + " for student: " + studentId));

        presetRepository.resetDefaultFlagsForStudent(studentId);
        preset.setIsDefault(true);

        LunchboxPreset updated = presetRepository.save(preset);
        updateStudentPrimaryBoxDimensions(preset.getStudent(), updated);

        return mapToResponse(updated);
    }

    @Transactional
    public void deletePreset(Long studentId, Long presetId) {
        LunchboxPreset preset = presetRepository.findByIdAndStudentId(presetId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("Preset not found with ID: " + presetId + " for student: " + studentId));

        boolean wasDefault = Boolean.TRUE.equals(preset.getIsDefault());

        presetRepository.delete(preset);
        presetRepository.flush();

        // If deleted preset was default, assign default to the most recent remaining preset
        if (wasDefault) {
            List<LunchboxPreset> remaining = presetRepository.findByStudentIdOrderByIsDefaultDescCreatedAtDesc(studentId);
            if (!remaining.isEmpty()) {
                LunchboxPreset newDefault = remaining.get(0);
                newDefault.setIsDefault(true);
                presetRepository.save(newDefault);
                updateStudentPrimaryBoxDimensions(preset.getStudent(), newDefault);
            }
        }
    }

    private void validateDimensions(String name, BigDecimal length, BigDecimal width, BigDecimal height) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Preset name is required");
        }
        if (length == null || length.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Length must be a positive number greater than 0");
        }
        if (width == null || width.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Width must be a positive number greater than 0");
        }
        if (height == null || height.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Height must be a positive number greater than 0");
        }
    }

    private void updateStudentPrimaryBoxDimensions(Student student, LunchboxPreset preset) {
        student.setBoxLength(preset.getLengthCm());
        student.setBoxWidth(preset.getWidthCm());
        student.setBoxDepth(preset.getHeightCm());
        BigDecimal vol = preset.getLengthCm().multiply(preset.getWidthCm()).multiply(preset.getHeightCm()).setScale(2, RoundingMode.HALF_UP);
        student.setBoxVolume(vol);
        studentRepository.save(student);
    }

    private LunchboxPresetResponse mapToResponse(LunchboxPreset p) {
        BigDecimal vol = BigDecimal.ZERO;
        if (p.getLengthCm() != null && p.getWidthCm() != null && p.getHeightCm() != null) {
            vol = p.getLengthCm().multiply(p.getWidthCm()).multiply(p.getHeightCm()).setScale(2, RoundingMode.HALF_UP);
        }
        return new LunchboxPresetResponse(
                p.getId(),
                p.getStudent().getId(),
                p.getStudent().getName(),
                p.getPresetName(),
                p.getLengthCm(),
                p.getWidthCm(),
                p.getHeightCm(),
                vol,
                p.getNotes(),
                p.getIsDefault(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
