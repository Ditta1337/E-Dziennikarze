package com.edziennikarze.gradebook.user.teacher;

import com.edziennikarze.gradebook.user.dto.User;
import com.edziennikarze.gradebook.user.teacher.dto.TeacherSubjectRow;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface TeacherRepository  extends ReactiveCrudRepository<User, UUID> {

    @Query("""
                    SELECT
                        u.id AS teacher_id,
                        u.name AS teacher_name,
                        u.surname AS teacher_surname,
                        u.created_at AS created_at,
                        u.address AS address,
                        u.email AS email,
                        u.contact AS contact,
                        u.image_base64 AS image_base_64,
                        st.subject_id AS subject_id,
                        s.name AS subject_name
                    FROM users u
                             LEFT JOIN subjects_taught st ON st.teacher_id = u.id
                             LEFT JOIN subjects s ON s.id = st.subject_id
                    WHERE u.active = 'true' and u.role = 'TEACHER'
                    ORDER BY u.id
            """)
    Flux<TeacherSubjectRow> findAllActiveTeachersWithSubjects();

}
