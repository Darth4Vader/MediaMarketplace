package backend.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "products")
public class Product implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private double buyPrice;
	
	@Column(nullable = false)
	private double rentPrice;
	
	@Column(precision=5, scale=2)
	private BigDecimal buyDiscount;
	
	@Column(precision=5, scale=2)
	private BigDecimal rentDiscount;
	
	/*
	 *     @CreationTimestamp
    private LocalDateTime createdAt;s
	 */
	
	
	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "movie", cascade = CascadeType.ALL)
    //@JoinColumn(name = "movie_id", nullable = false)
	@ManyToOne(optional = false)
	@JoinColumn(name = "movie_id", referencedColumnName = "id")
    private Movie movie;

	public Product() {
	}

	public Long getId() {
		return id;
	}
	
}
