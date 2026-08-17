package vn.edu.crs.course_service.service;

import vn.edu.crs.course_service.dto.CourseDTO;
import vn.edu.crs.course_service.entity.Course;
import vn.edu.crs.course_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    // 1. Lấy tất cả môn học
    public List<CourseDTO> getAll() {
        return courseRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 2. Lấy môn học theo ID
    public CourseDTO getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc id = " + id));
        return toDTO(course);
    }

    // 3. Tạo mới môn học
    public CourseDTO create(CourseDTO dto) {
        if (courseRepository.existsByTenMonHocIgnoreCase(dto.getTenMonHoc())) {
            throw new IllegalArgumentException("Ten mon hoc da ton tai");
        }
        Course course = new Course();
        course.setTenMonHoc(dto.getTenMonHoc());
        course.setSoTinChi(dto.getSoTinChi());
        course.setSoChoToiDa(dto.getSoChoToiDa());
        // Nghiệp vụ: khi tạo mới, số chỗ còn lại luôn bằng số chỗ tối đa
        course.setSoChoConLai(dto.getSoChoToiDa());

        return toDTO(courseRepository.save(course));
    }

    // 4. Cập nhật môn học
    public CourseDTO update(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc id = " + id));

        course.setTenMonHoc(dto.getTenMonHoc());
        course.setSoTinChi(dto.getSoTinChi());
        course.setSoChoToiDa(dto.getSoChoToiDa());

        return toDTO(courseRepository.save(course));
    }

    // 5. Xóa môn học
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NoSuchElementException("Khong tim thay mon hoc id = " + id);
        }
        courseRepository.deleteById(id);
    }

    // 6. Giữ chỗ (giảm số chỗ còn lại đi 1)
    public CourseDTO reserveSeat(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc id = " + id));

        if (course.getSoChoConLai() <= 0) {
            throw new IllegalStateException("Mon hoc da het cho");
        }

        course.setSoChoConLai(course.getSoChoConLai() - 1);
        return toDTO(courseRepository.save(course));
    }

    // 7. Nhả chỗ / Hủy chỗ (tăng số chỗ còn lại lên 1)
    public CourseDTO releaseSeat(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc id = " + id));

        if (course.getSoChoConLai() < course.getSoChoToiDa()) {
            course.setSoChoConLai(course.getSoChoConLai() + 1);
        }

        return toDTO(courseRepository.save(course));
    }

    // Hàm phụ chuyển đổi từ Course sang CourseDTO
    private CourseDTO toDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getTenMonHoc(),
                course.getSoTinChi(),
                course.getSoChoToiDa(),
                course.getSoChoConLai()
        );
    }
}