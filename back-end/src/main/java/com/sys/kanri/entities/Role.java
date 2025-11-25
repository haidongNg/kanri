package com.sys.kanri.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sys.kanri.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity()
@Table(name = "roles")
public class Role extends BaseEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleType name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnoreProperties("role")
    private List<Member> members = new ArrayList<>();

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role roleObj)) return false;
        return id != null && id.equals(roleObj.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getAuthority();
    }
}
