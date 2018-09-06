package com.aizhixin.cloud.paycallback.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@MappedSuperclass
@ToString
public abstract class AbstractOnlyIdEntity implements java.io.Serializable {
	private static final long serialVersionUID = -5481883489404960814L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter @Setter	protected Long id;
}
