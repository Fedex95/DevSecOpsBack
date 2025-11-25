package com.tienda.kpback.Repository;
import com.tienda.kpback.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}
