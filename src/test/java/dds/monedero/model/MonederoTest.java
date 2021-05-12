package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;
  private final LocalDate fechaPasada = LocalDate.of(2020,3,16);
  private final Movimiento movimientoPasado = new Movimiento(fechaPasada,300,true);

  @BeforeEach
  public void init() {
    cuenta = new Cuenta();
  }

  @Test
  public void PonerDineroEnUnaCuentaVacia() {
    cuenta.poner(1500);
    assertEquals(1500,cuenta.getSaldo());
  }

  @Test
  public void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  public void SeHacenTresDepositosEnUnaCuentaVacia() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(3856,cuenta.getSaldo());
  }

  @Test
  public void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  public void ExtraerDineroDeUnaCuentaDe1000(){
    cuenta.poner(1000);
    cuenta.sacar(100);
    assertEquals(900,cuenta.getSaldo());
  }

  @Test
  public void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  public void VerExtraccionDeUnaFecha(){
    cuenta.setSaldo(1000);
    cuenta.agregarMovimiento(fechaPasada,300,false);
    assertEquals(300,cuenta.getMontoExtraidoEn(fechaPasada));
  }

  @Test
  public void MovimientoQuedaRegistradoEnCuenta(){
    cuenta.agregarMovimiento(fechaPasada,300,true);
    assertFalse(cuenta.getMovimientos().isEmpty());
  }
}