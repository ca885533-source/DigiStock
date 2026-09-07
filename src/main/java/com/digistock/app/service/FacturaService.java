package com.digistock.app.service;

import com.digistock.app.dto.FacturaDTO;
import com.digistock.app.dto.ItemFacturaDTO;
import com.digistock.app.model.EstadoFactura;
import com.digistock.app.model.Factura;
import com.digistock.app.model.ItemFactura;
import com.digistock.app.model.Producto;
import com.digistock.app.repository.FacturaRepository;
import com.digistock.app.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que contiene la logica de negocio del modulo de facturacion.
 *
 * Responsabilidades principales:
 *  - Validar que cada producto tenga stock suficiente antes de facturar.
 *  - Calcular subtotal, IVA (19%) y total a partir del precio real del
 *    producto en base de datos (nunca del valor enviado por el formulario).
 *  - Descontar el stock vendido del inventario.
 *  - Generar el numero consecutivo de la factura.
 *  - Permitir anular una factura, reintegrando el stock correspondiente.
 *
 * Se mantiene separada del controlador (patron MVC + capa de servicio),
 * igual que UsuarioService en el modulo de autenticacion.
 */
@Service
public class FacturaService {

    /** Porcentaje de IVA aplicado a todas las ventas (Colombia, tarifa general). */
    private static final double PORCENTAJE_IVA = 0.19;

    private final FacturaRepository facturaRepository;
    private final ProductoRepository productoRepository;

    public FacturaService(FacturaRepository facturaRepository, ProductoRepository productoRepository) {
        this.facturaRepository = facturaRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Registra una nueva factura a partir de los datos del formulario.
     *
     * @param datos datos del cliente y los items capturados en el formulario
     * @param vendedor nombre de usuario del vendedor autenticado que factura
     * @return la factura ya guardada, con totales calculados
     * @throws IllegalArgumentException si un producto no existe o no hay
     *         stock suficiente para la cantidad solicitada
     */
    public Factura crear(FacturaDTO datos, String vendedor) {
        List<ItemFactura> items = new ArrayList<>();
        double subtotal = 0.0;

        for (ItemFacturaDTO itemDto : datos.getItems()) {
            // Se ignoran filas vacias que pudieron quedar en el formulario
            // (por ejemplo si el usuario agrego una fila y no selecciono producto)
            if (itemDto.getProductoId() == null || itemDto.getProductoId().isBlank() || itemDto.getCantidad() <= 0) {
                continue;
            }

            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("El producto seleccionado no existe"));

            if (producto.getStockDisponible() < itemDto.getCantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para \"" + producto.getNombre() + "\". Disponible: "
                                + producto.getStockDisponible());
            }

            double subtotalItem = producto.getPrecio() * itemDto.getCantidad();
            items.add(new ItemFactura(
                    producto.getId(),
                    producto.getNombre(),
                    itemDto.getCantidad(),
                    producto.getPrecio(),
                    subtotalItem
            ));

            subtotal += subtotalItem;

            // Descuenta el stock vendido del inventario
            producto.setStockDisponible(producto.getStockDisponible() - itemDto.getCantidad());
            productoRepository.save(producto);
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un producto valido");
        }

        double iva = subtotal * PORCENTAJE_IVA;

        Factura factura = new Factura();
        factura.setNumeroFactura(generarNumeroFactura());
        factura.setVendedor(vendedor);
        factura.setNombreCliente(datos.getNombreCliente());
        factura.setDocumentoCliente(datos.getDocumentoCliente());
        factura.setItems(items);
        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setTotal(subtotal + iva);
        factura.setEstado(EstadoFactura.PAGADA);

        return facturaRepository.save(factura);
    }

    /** Lista todas las facturas, de la mas reciente a la mas antigua. */
    public List<Factura> listarTodas() {
        return facturaRepository.findAllByOrderByFechaDesc();
    }

    /** Busca una factura por su id; usado para mostrar el detalle/impresion. */
    public Factura buscarPorId(String id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
    }

    /**
     * Anula una factura y reintegra al inventario el stock de cada item,
     * ya que la venta ya no se contabiliza.
     */
    public void anular(String id) {
        Factura factura = buscarPorId(id);

        if (factura.getEstado() == EstadoFactura.ANULADA) {
            return; // ya estaba anulada, no se repite el reintegro de stock
        }

        for (ItemFactura item : factura.getItems()) {
            productoRepository.findById(item.getProductoId()).ifPresent(producto -> {
                producto.setStockDisponible(producto.getStockDisponible() + item.getCantidad());
                productoRepository.save(producto);
            });
        }

        factura.setEstado(EstadoFactura.ANULADA);
        facturaRepository.save(factura);
    }

    /**
     * Genera un numero consecutivo simple para la factura, con el formato
     * FAC-000001. Se basa en el total de facturas existentes.
     *
     * NOTA: para un entorno con varios vendedores facturando al mismo
     * tiempo, lo ideal seria un contador atomico en MongoDB; aqui se usa
     * la forma simple porque el alcance de esta evidencia es el modulo
     * de front-end, no la concurrencia de alto volumen.
     */
    private String generarNumeroFactura() {
        long consecutivo = facturaRepository.count() + 1;
        return String.format("FAC-%06d", consecutivo);
    }
}
