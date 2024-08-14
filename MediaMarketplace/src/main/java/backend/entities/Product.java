package backend.entities;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {
	
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
	
	@OneToOne(optional = false)
	@JoinColumn(name = "movie_id")
    private Movie movie;

	public Product() {
	}

	public Long getId() {
		return id;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public double getRentPrice() {
		return rentPrice;
	}

	public BigDecimal getBuyDiscount() {
		return buyDiscount;
	}

	public BigDecimal getRentDiscount() {
		return rentDiscount;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public void setRentPrice(double rentPrice) {
		this.rentPrice = rentPrice;
	}
	
	public void setBuyDiscount(double buyDiscount) {
		setBuyDiscount(new BigDecimal(buyDiscount));
	}

	public void setBuyDiscount(BigDecimal buyDiscount) {
		this.buyDiscount = buyDiscount;
	}
	
	public void setRentDiscount(double rentDiscount) {
		setRentDiscount(new BigDecimal(rentDiscount));
	}

	public void setRentDiscount(BigDecimal rentDiscount) {
		this.rentDiscount = rentDiscount;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}
}
