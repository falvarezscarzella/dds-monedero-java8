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
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
    Movimiento deposito = new Movimiento(LocalDate.now(), cuanto, true);
    agregarMovimiento(deposito);
  }

  public void sacar(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoEn(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
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

}


/*

*En getMontoExtraidoA(1) se puede simplificar esa logica del filter abstrayendo la funcion booleana (Long Method?)

*En el metodo poner(1) y sacar(1) tambien se puede abtraer una parte de la logica de las validaciones
en un metodo nuevo (Long method?)
*/