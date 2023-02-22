package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long>
        , JpaSpecificationExecutor<Employee> {
     @EntityGraph(attributePaths = "permissions", type = EntityGraph.EntityGraphType.LOAD)
     Optional<Employee> findByUsername(String username);

     @Modifying
     @Query(value = """
          UPDATE isikato_employee SET 
          username = :#{#employee.username},
          name = :#{#employee.name},
          phone = :#{#employee.phone},
          email = :#{#employee.email}
          WHERE id = :#{#employee.id}
          """,
     nativeQuery = true)
     int update(@Param("employee") Employee employee);

     @Modifying
     @Query(value = """
          UPDATE isikato_employee SET 
          username = :#{#employee.username},
          password = :#{#employee.password},
          name = :#{#employee.name},
          phone = :#{#employee.phone},
          email = :#{#employee.email}
          WHERE id = :#{#employee.id}
          """,
             nativeQuery = true)
     int updateWithPassword(@Param("employee") Employee employee);


     long removeById(long id);
}
