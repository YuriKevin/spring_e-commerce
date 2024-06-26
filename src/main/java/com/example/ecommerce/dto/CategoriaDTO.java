package com.example.ecommerce.dto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoriaDTO {
	private Long id;
	private String titulo;
    private List<ProdutoDTO> produtos;
}
