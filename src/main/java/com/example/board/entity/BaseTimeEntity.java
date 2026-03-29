package com.example.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/*
 * @MappedSuperclass: 이 클래스를 상속받는 자식 엔티티(예: Post)에게 자신의 필드(createdAt, updatedAt)를 데이터베이스의 컬럼으로 인식하도록 내려주는 어노테이션이다.
 *
 * @EntityListeners(AuditingEntityListener.class): 이 클래스에 스프링 데이터 JPA의 감사(Auditing) 기능을 포함시킨다는 선언이다.
 * 엔티티가 데이터베이스에 추가되거나 변경될 때 자동으로 시간을 기록하는 엔진을 달아준다고 보면 된다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    /*
     * @CreatedDate / @LastModifiedDate: 각각 엔티티가 처음 저장될 때, 그리고 값이 변경될 때의 시간을 자동으로 주입해 주는 어노테이션이다.

     * @Column(updatable = false): 생성 시간은 한 번 만들어지면 절대 수정되어서는 안 되므로, 데이터베이스 차원에서 업데이트를 막아두는 안전장치다.
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
