package com.tialola.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "fecha_login")
    private LocalDateTime fechaLogin;
    
    @Column(name = "fecha_logout")
    private LocalDateTime fechaLogout;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @PrePersist
    protected void onCreate() {
        fechaLogin = LocalDateTime.now();
    }
}
