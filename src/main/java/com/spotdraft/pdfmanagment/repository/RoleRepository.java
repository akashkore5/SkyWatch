package com.spotdraft.pdfmanagment.repository;

import com.spotdraft.pdfmanagment.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}