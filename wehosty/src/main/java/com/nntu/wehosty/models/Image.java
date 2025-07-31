// package com.nntu.wehosty.models;
// import jakarta.persistence.*;
// import java.time.LocalDateTime;
// import lombok.Data;
// @Entity
// @Table(name = "image")
// @Data
// public class Image {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "id")
//     private Long id;
//     @Column(name = "link")
//     private String link;
//     @Column(name = "date_add")
//     private LocalDateTime date;
//     @Column(name = "date_update")
//     private LocalDateTime date_update;
//     @PrePersist
//     private void init() {
//         date = LocalDateTime.now();
//     }
// }
