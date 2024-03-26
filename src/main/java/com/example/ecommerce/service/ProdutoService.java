package com.example.ecommerce.service;
import java.util.List;
import java.util.ArrayList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.ecommerce.dto.CategoriaDTO;
import com.example.ecommerce.dto.ProdutoDTO;
import com.example.ecommerce.model.Produto;
import com.example.ecommerce.model.ProdutoComprado;
import com.example.ecommerce.repository.ProdutoRepository;
import com.example.ecommerce.requests.ProdutoPostRequestBody;
import com.example.ecommerce.requests.ProdutoPutRequestBody;
import jakarta.transaction.Transactional;
import com.example.ecommerce.model.Categoria;
import com.example.ecommerce.model.Loja;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoService {
	private final ProdutoRepository produtoRepository;
	private final LojaService lojaService;
	
	@Transactional
	public Produto encontrarPorIdOuExcecao(Long id){
		return produtoRepository.findById(id)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto Não encontrado"));
	}
	
	@Transactional
	public ProdutoDTO encontrarPorIdDTO(Long id){
		Produto produto = encontrarPorIdOuExcecao(id);
		 ProdutoDTO produtoDTO = ProdutoDTO.builder()
				.id(produto.getId())
				.titulo(produto.getTitulo())
				.valor(produto.getValor())
				.imagens(produto.getImagens())
				.detalhes(produto.getDetalhes())
				.quantidadeVendida(produto.getQuantidadeVendida())
				.avaliacao(produto.getAvaliacao())
				.disponivel(produto.isDisponivel())
				.build();
		 return produtoDTO;
	}
	
	@Transactional
	public List<Produto> listarPorTitulo(String titulo, int pagina, int itens){
		return produtoRepository.findByTitulo(titulo, PageRequest.of(pagina, itens));
	}
	
	@Transactional
	public List<ProdutoDTO> listarPorTituloDTO(String titulo, int pagina, int itens){
		List<Produto> produtosSalvos = listarPorTitulo(titulo, pagina, itens);
		verificarListaVaziaExcecao(produtosSalvos);
		List<ProdutoDTO> produtosDTO = transformarProdutosEmDTO(produtosSalvos);
		return produtosDTO;
	}
	
	@Transactional
	public List<Produto> listarPorCategoria(String categoria, int pagina, int itens){
		return produtoRepository.findByCategoria(categoria, PageRequest.of(pagina, itens));		
	}
	
	@Transactional
	public List<ProdutoDTO> listarPorCategoriaDTO(String categoria, int pagina, int itens){
		List<Produto> produtosSalvos = listarPorCategoria(categoria, pagina, itens);
		verificarListaVaziaExcecao(produtosSalvos);
		List<ProdutoDTO> produtosDTO = transformarProdutosEmDTO(produtosSalvos);
		return produtosDTO;
	}
	
	@Transactional
	public List<Produto> listarProdutosDeUmaloja(String loja, int pagina, int itens){
		return produtoRepository.findByNomeLoja(loja, PageRequest.of(pagina, itens));		
	}
	
	@Transactional
	public List<ProdutoDTO> listarProdutosDeUmalojaDTO(String loja, int pagina, int itens){
		List<Produto> produtosSalvos = listarProdutosDeUmaloja(loja, pagina, itens);
		verificarListaVaziaExcecao(produtosSalvos);
		List<ProdutoDTO> produtosDTO = transformarProdutosEmDTO(produtosSalvos);
		return produtosDTO;
	}
	
	@Transactional
	public List<ProdutoDTO> listarProdutosMaisVendidos(String loja){
		List<Produto> produtosSalvos = produtoRepository.findTop10ByOrderByQuantidadeVendidaDesc();
		verificarListaVaziaExcecao(produtosSalvos);
		List<ProdutoDTO> produtosDTO = transformarProdutosEmDTO(produtosSalvos);
		 return produtosDTO;
	}
	
	@Transactional
	public List<ProdutoDTO> listarProdutosMaisVendidosDeUmaLoja(String loja){
		List<Produto> produtosSalvos = produtoRepository.findTop10ByNomeLojaOrderByQuantidadeVendidaDesc(loja);
		verificarListaVaziaExcecao(produtosSalvos);
		List<ProdutoDTO> produtosDTO = transformarProdutosEmDTO(produtosSalvos);
		return produtosDTO;
	}
	
	public List<ProdutoDTO> transformarProdutosEmDTO(List<Produto> produtos){
		List<ProdutoDTO> produtosDTO = new ArrayList<>();
		for(Produto produtoSalvo : produtos) {
			ProdutoDTO produtoDTO = ProdutoDTO.builder()
					.id(produtoSalvo.getId())
					.titulo(produtoSalvo.getTitulo())
					.valor(produtoSalvo.getValor())
					.imagens(produtoSalvo.getImagens())
					.detalhes(produtoSalvo.getDetalhes())
					.quantidade(produtoSalvo.getQuantidade())
					.quantidadeVendida(produtoSalvo.getQuantidadeVendida())
					.avaliacao(produtoSalvo.getAvaliacao())
					.disponivel(produtoSalvo.isDisponivel())
					.build();
			produtosDTO.add(produtoDTO);
		}
		return produtosDTO;
	}
	
	public ProdutoDTO transformarUmProdutoEmDTO(Produto produto){
			ProdutoDTO produtoDTO = ProdutoDTO.builder()
					.id(produto.getId())
					.titulo(produto.getTitulo())
					.valor(produto.getValor())
					.imagens(produto.getImagens())
					.detalhes(produto.getDetalhes())
					.quantidade(produto.getQuantidade())
					.quantidadeVendida(produto.getQuantidadeVendida())
					.avaliacao(produto.getAvaliacao())
					.disponivel(produto.isDisponivel())
					.build();
		
		return produtoDTO;
	}
	
	@Transactional
	public Produto novoProduto(ProdutoPostRequestBody produto) {
		if(produto.getImagens().size()>6) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "é permitido no máximo 6 imagens por produto.");
		}
		Loja loja = lojaService.encontrarPorIdOuExcecao(produto.getLojaId());
		return produtoRepository.save(Produto.builder()
				.titulo(produto.getTitulo())
				.valor(produto.getValor())
				.categoria(produto.getCategoria())
				.nomeLoja(loja.getNome())
				.imagens(produto.getImagens())
				.loja(loja)
				.detalhes(produto.getDetalhes())
				.quantidadeVendida(0L)
				.quantidadeAvaliacoes(0L)
				.somaAvaliacoes(0L)
				.avaliacao(null)
				.build());
	}
	
	@Transactional
	public void atualizarProduto(ProdutoPutRequestBody produto) {
		Produto produtoSalvo = encontrarPorIdOuExcecao(produto.getId());
		produtoSalvo.setTitulo(produto.getTitulo());
		produtoSalvo.setValor(produto.getValor());
		produtoSalvo.setCategoria(produto.getCategoria());
		if(produto.getImagens().size()>6) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "é permitido no máximo 6 imagens por produto.");
		}
		produtoSalvo.setImagens(produto.getImagens());
		produtoSalvo.setDetalhes(produto.getDetalhes());
		produtoRepository.save(produtoSalvo);
	}
	
	@Transactional
	public void deletarProduto(Long id){
		produtoRepository.delete(encontrarPorIdOuExcecao(id));
	}
	
	@Transactional
	public void avaliarProduto(Produto produto, int avaliacao) {
		produto.setQuantidadeAvaliacoes(produto.getQuantidadeAvaliacoes()+1);
		produto.setSomaAvaliacoes(produto.getSomaAvaliacoes()+avaliacao);
		produto.setAvaliacao(calcularAvaliacao(produto.getSomaAvaliacoes(), produto.getQuantidadeAvaliacoes()));
		produtoRepository.save(produto);
	}
	
	@Transactional
	public Double calcularAvaliacao(Long soma, Long quantidade) {
		return (double) (soma/quantidade);
	}
	
	@Transactional
	public void adicionarQuantidadeVendida(ProdutoComprado produtoComprado){
		Produto produtoSalvo = encontrarPorIdOuExcecao(produtoComprado.getProduto().getId());
		produtoSalvo.setQuantidadeVendida(produtoSalvo.getQuantidadeVendida()+ produtoComprado.getQuantidade());
		produtoRepository.save(produtoSalvo);
		
	}
	
	@Transactional
	public void desativarProdutosDeUmaLoja(Long id){
		Loja lojaSalva = lojaService.encontrarPorIdOuExcecao(id);
		produtoRepository.setDisponivelFalseByLoja(lojaSalva);
	}
	
	@Transactional
	public void ativarProdutosDeUmaLoja(Long id){
		Loja lojaSalva = lojaService.encontrarPorIdOuExcecao(id);
		produtoRepository.setDisponivelTrueByLoja(lojaSalva);
	}
	
	@Transactional
	public void produtoVendido(Produto produto, int quantidade){
		produto.setQuantidade(produto.getQuantidade()-quantidade);
		produto.setQuantidadeAvaliacoes(produto.getQuantidadeVendida() + quantidade);
		lojaService.adicionarCredito(produto.getLoja().getId(), produto.getValor() * quantidade);
		produtoRepository.save(produto);
	}
	
	public void verificarListaVaziaExcecao(List<Produto> produtos) {
		if(produtos.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "0 resultados encontrados para a pesquisa.");
		}
	}
	
	@Transactional
	public List<CategoriaDTO> listarProdutosCategorizadosDeUmaLoja(Long id) {
		Loja loja = lojaService.encontrarPorIdOuExcecao(id);
		List<CategoriaDTO> categorias = transformarCategoriasEmDTO(loja.getCategorias());
		return categorias;
	}
	
	public List<CategoriaDTO> transformarCategoriasEmDTO(List<Categoria> categorias){
		List<CategoriaDTO> categoriasDTO = new ArrayList<>();
		for(Categoria categoriaSalva : categorias) {
			List<ProdutoDTO> produtosDTO = transformarProdutosEmDTO(categoriaSalva.getProdutos());
			CategoriaDTO categoriaDTO = CategoriaDTO.builder()
					.titulo(categoriaSalva.getTitulo())
					.produtos(produtosDTO)
					.build();
					categoriasDTO.add(categoriaDTO);
		}
		return categoriasDTO;
	}
	
}