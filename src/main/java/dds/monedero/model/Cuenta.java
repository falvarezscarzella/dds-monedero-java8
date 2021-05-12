package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta(){};

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    verificarMontoNegativo(cuanto);
    verificarDepositosDiarios();
    Movimiento deposito = new Movimiento(LocalDate.now(), cuanto, true);
    agregarMovimiento(deposito);
  }

  public void sacar(double cuanto) {
    verificarMontoNegativo(cuanto);
    verificarExtraccionMasGrandeQueElSaldo(cuanto);
    verificarLimiteDeExtraccion(cuanto);
    Movimiento extraccion = new Movimiento(LocalDate.now(), cuanto, false);
    agregarMovimiento(extraccion);
  }

  public void agregarMovimiento(Movimiento movimiento) {
    hacerMovimiento(movimiento);
    movimientos.add(movimiento);
  }


  public void hacerMovimiento(Movimiento movimiento){
    if(movimiento.isDeposito()) saldo+= movimiento.getMonto();
    else saldo-= movimiento.getMonto();
  }

  public long cantidadDepositosDeHoy(){
    return getMovimientos().stream().
        filter(movimiento -> movimiento.fueDepositado(LocalDate.now())).count();
  }

 // GETTERS Y SETTERS

  public double getMontoExtraidoEn(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito())
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  //METODOS DE VERIFICACION

  public void verificarMontoNegativo(double monto){
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void verificarExtraccionMasGrandeQueElSaldo(double extraccion){
    if (getSaldo() - extraccion < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public void verificarLimiteDeExtraccion(double extraccion){
    double montoExtraidoHoy = getMontoExtraidoEn(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (extraccion > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public void verificarDepositosDiarios(){
    if (cantidadDepositosDeHoy() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }
}


/*
*En getMontoExtraidoA(1) se puede simplificar esa logica del filter abstrayendo la funcion booleana (Long Method?)
*/