package com.edziennikarze.gradebook.plan.manual;

import com.edziennikarze.gradebook.plan.manual.dto.ManualPlan;
import com.edziennikarze.gradebook.plan.manual.dto.ManualPlanSummary;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ManualPlanRepository extends ReactiveCrudRepository<ManualPlan, UUID> {

    @Query("""
            SELECT
                mp.id as id,
                mp.name as name,
                u.name as office_worker_name,
                u.surname as office_worker_surname,
                mp.plan_calculation_id as plan_calculation_id,
                mp.created_at as created_at
            FROM manual_plans as mp
            INNER JOIN users as u
            ON mp.office_worker_id = u.id
            """)
   Flux<ManualPlanSummary> findAllSummary();

}
