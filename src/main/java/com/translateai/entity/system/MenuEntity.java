package com.translateai.entity.system;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

@DynamicUpdate
@Entity
@Table(name = "menu")
@Data
public class MenuEntity {

    /** ID menu */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Loại trang (Admin, User...) */
    private String siteType;

    /** Tên menu */
    private String name;

    /** Đường dẫn route */
    private String route;

    /** ID menu cha (menu cấp trên) */
    private Long parentId;

    /** URL phụ */
    private String subUrl;

    /** Thứ tự hiển thị */
    private Integer displayOrder;

    /** Cờ xóa (Y/N) */
    private String deleteFlag;

    /** Danh sách vai trò có quyền truy cập menu này */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "menu_role", joinColumns = {@JoinColumn(name = "menuId")}, inverseJoinColumns = {@JoinColumn (name = "roleId")})
    @ToString.Exclude
    private Set<RoleEntity> roleSet = new HashSet<>();

}
