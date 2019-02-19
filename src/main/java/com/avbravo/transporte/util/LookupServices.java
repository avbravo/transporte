/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.transporte.util;

import java.util.Date;
import javax.ejb.Stateless;

/**
 *
 * @author avbravo
 */
@Stateless
public class LookupServices {

    private String text;
    private String id;

    private String idunidad;
    private String idsemestre;
    private String username;
    private String idtipogira;
    private String idtiposolicitud;
    private String idtipovehiculo;
    private String idestatus;
private String idsugerencia;
    private String idrol;

    private String nombre;
    private String pais;
    private String descripcion;
    private String cedula;
    private String apellido;
    private String apellidomaterno;
    private String apellidopaterno;
    private String cliente;
    private String proveedor;
    private String usuario;
    private String rol;
    private String paciente;
    private String cargo;
    private String provincia;
    private String distrito;
    private String continente;
    private String planeta;
    private String marca;
    private String placa;
    private String vehiculo;
    private String agente;
    private String comprador;
    private String vendedor;
    private String banco;
    private String cuenta;
    private String sucursal;
    private String lugardestino;
    private String comentarios;

    //fechas
    private Date fecha;
    private Date fechaDesde;
    private Date fechaHasta;
    private Date fechaincio;
    private Date fechafin;
    private Date fechanacimiento;
    private Date fechamuerte;
    private Date fechabaja;
    private Date fechaalta;

    /*
    numeros
     */
        private Integer idconductor;
    private Integer idfacultad;
    private Integer idsolicitud;
    private Integer idvehiculo;
    private Integer idcarrera;
    private Integer idviaje;
    private Integer idvacacion;
    private Integer idpermiso;
    private Integer idtiempoextra;
    private Integer numero;
    private Integer numeroDesde;
    private Integer numeroHasta;

    //Double
    private Double salario;
    private Double itbms;
    private Double ventas;
    private Double doubledesde;
    private Double doublehasta;
    private Double subtotal;
    private Double total;
    private Double saldo;
    private Double vuelto;
    private Double monto;
    private Double apagar;
    private Double cambio;
    private Double sueltobruto;
    private Double sueldoneto;
    private Double exoneracion;
    private Double totalexoneracion;

    public LookupServices() {
    }

    public String getIdsugerencia() {
        return idsugerencia;
    }

    public void setIdsugerencia(String idsugerencia) {
        this.idsugerencia = idsugerencia;
    }

    public String getLugardestino() {
        return lugardestino;
    }

    public void setLugardestino(String lugardestino) {
        this.lugardestino = lugardestino;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    
    
    
    public Integer getIdcarrera() {
        return idcarrera;
    }

    public String getIdsemestre() {
        return idsemestre;
    }

    public void setIdsemestre(String idsemestre) {
        this.idsemestre = idsemestre;
    }

    public String getIdrol() {
        return idrol;
    }

    public void setIdrol(String idrol) {
        this.idrol = idrol;
    }

    public String getIdtipogira() {
        return idtipogira;
    }

    public void setIdtipogira(String idtipogira) {
        this.idtipogira = idtipogira;
    }

  
    
    
    
    public void setIdcarrera(Integer idcarrera) {
        this.idcarrera = idcarrera;
    }

    public String getIdtiposolicitud() {
        return idtiposolicitud;
    }

    public void setIdtiposolicitud(String idtiposolicitud) {
        this.idtiposolicitud = idtiposolicitud;
    }

    public String getIdestatus() {
        return idestatus;
    }

    public Integer getIdsolicitud() {
        return idsolicitud;
    }

    public void setIdsolicitud(Integer idsolicitud) {
        this.idsolicitud = idsolicitud;
    }

    
    
    public void setIdestatus(String idestatus) {
        this.idestatus = idestatus;
    }

    public Integer getIdconductor() {
        return idconductor;
    }

    public void setIdconductor(Integer idconductor) {
        this.idconductor = idconductor;
    }

  

    public Integer getIdvehiculo() {
        return idvehiculo;
    }

    public void setIdvehiculo(Integer idvehiculo) {
        this.idvehiculo = idvehiculo;
    }

    public Integer getIdviaje() {
        return idviaje;
    }

    public void setIdviaje(Integer idviaje) {
        this.idviaje = idviaje;
    }

    public Integer getIdtiempoextra() {
        return idtiempoextra;
    }

    public void setIdtiempoextra(Integer idtiempoextra) {
        this.idtiempoextra = idtiempoextra;
    }

    public String getIdtipovehiculo() {
        return idtipovehiculo;
    }

    public void setIdtipovehiculo(String idtipovehiculo) {
        this.idtipovehiculo = idtipovehiculo;
    }

    public Integer getIdvacacion() {
        return idvacacion;
    }

    public void setIdvacacion(Integer idvacacion) {
        this.idvacacion = idvacacion;
    }

    public Integer getIdpermiso() {
        return idpermiso;
    }

    public void setIdpermiso(Integer idpermiso) {
        this.idpermiso = idpermiso;
    }

    public String getIdunidad() {
        return idunidad;
    }

    public void setIdunidad(String idunidad) {
        this.idunidad = idunidad;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getApellidomaterno() {
        return apellidomaterno;
    }

    public void setApellidomaterno(String apellidomaterno) {
        this.apellidomaterno = apellidomaterno;
    }

    public String getApellidopaterno() {
        return apellidopaterno;
    }

    public void setApellidopaterno(String apellidopaterno) {
        this.apellidopaterno = apellidopaterno;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getContinente() {
        return continente;
    }

    public void setContinente(String continente) {
        this.continente = continente;
    }

    public String getPlaneta() {
        return planeta;
    }

    public void setPlaneta(String planeta) {
        this.planeta = planeta;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getAgente() {
        return agente;
    }

    public void setAgente(String agente) {
        this.agente = agente;
    }

    public String getComprador() {
        return comprador;
    }

    public void setComprador(String comprador) {
        this.comprador = comprador;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Date getFechaincio() {
        return fechaincio;
    }

    public void setFechaincio(Date fechaincio) {
        this.fechaincio = fechaincio;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public Date getFechanacimiento() {
        return fechanacimiento;
    }

    public void setFechanacimiento(Date fechanacimiento) {
        this.fechanacimiento = fechanacimiento;
    }

    public Date getFechamuerte() {
        return fechamuerte;
    }

    public void setFechamuerte(Date fechamuerte) {
        this.fechamuerte = fechamuerte;
    }

    public Date getFechabaja() {
        return fechabaja;
    }

    public void setFechabaja(Date fechabaja) {
        this.fechabaja = fechabaja;
    }

    public Date getFechaalta() {
        return fechaalta;
    }

    public void setFechaalta(Date fechaalta) {
        this.fechaalta = fechaalta;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getNumeroDesde() {
        return numeroDesde;
    }

    public void setNumeroDesde(Integer numeroDesde) {
        this.numeroDesde = numeroDesde;
    }

    public Integer getNumeroHasta() {
        return numeroHasta;
    }

    public void setNumeroHasta(Integer numeroHasta) {
        this.numeroHasta = numeroHasta;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public Double getItbms() {
        return itbms;
    }

    public void setItbms(Double itbms) {
        this.itbms = itbms;
    }

    public Double getVentas() {
        return ventas;
    }

    public void setVentas(Double ventas) {
        this.ventas = ventas;
    }

    public Double getDoubledesde() {
        return doubledesde;
    }

    public void setDoubledesde(Double doubledesde) {
        this.doubledesde = doubledesde;
    }

    public Double getDoublehasta() {
        return doublehasta;
    }

    public void setDoublehasta(Double doublehasta) {
        this.doublehasta = doublehasta;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Double getVuelto() {
        return vuelto;
    }

    public void setVuelto(Double vuelto) {
        this.vuelto = vuelto;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Double getApagar() {
        return apagar;
    }

    public void setApagar(Double apagar) {
        this.apagar = apagar;
    }

    public Double getCambio() {
        return cambio;
    }

    public void setCambio(Double cambio) {
        this.cambio = cambio;
    }

    public Double getSueltobruto() {
        return sueltobruto;
    }

    public void setSueltobruto(Double sueltobruto) {
        this.sueltobruto = sueltobruto;
    }

    public Double getSueldoneto() {
        return sueldoneto;
    }

    public void setSueldoneto(Double sueldoneto) {
        this.sueldoneto = sueldoneto;
    }

    public Double getExoneracion() {
        return exoneracion;
    }

    public void setExoneracion(Double exoneracion) {
        this.exoneracion = exoneracion;
    }

    public Double getTotalexoneracion() {
        return totalexoneracion;
    }

    public void setTotalexoneracion(Double totalexoneracion) {
        this.totalexoneracion = totalexoneracion;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getIdfacultad() {
        return idfacultad;
    }

    public void setIdfacultad(Integer idfacultad) {
        this.idfacultad = idfacultad;
    }

    
    
}
