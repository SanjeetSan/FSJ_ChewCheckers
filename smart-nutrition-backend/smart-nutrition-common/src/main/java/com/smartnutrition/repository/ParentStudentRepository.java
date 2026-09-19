package com.smartnutrition.repository;

import com.smartnutrition.entity.ParentStudent;
import com.smartnutrition.entity.Student;
import com.smartnutrition.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentStudentRepository extends JpaRepository<ParentStudent, Long> {

    List<ParentStudent> findByParentId(Long parentId);

    List<ParentStudent> findByStudentId(Long studentId);

    boolean existsByParentAndStudent(User parent, Student student);

    void deleteByParentAndStudent(User parent, Student student);

    boolean existsByStudent(Student student);
}
