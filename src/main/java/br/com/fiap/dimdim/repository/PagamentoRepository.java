package br.com.fiap.dimdim.repository;

import br.com.fiap.dimdim.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @Query("select p from Pagamento p join fetch p.cliente order by p.id desc")
    List<Pagamento> listarTodos();

    @Query("select p from Pagamento p join fetch p.cliente where p.cliente.id = :clienteId order by p.id desc")
    List<Pagamento> listarPorCliente(@Param("clienteId") Long clienteId);

    boolean existsByClienteId(Long clienteId);
}
