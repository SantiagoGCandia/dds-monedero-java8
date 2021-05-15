package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private Integer limiteExtraccionDiario;
  private Integer limiteDepositoDiario;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
    limiteExtraccionDiario = 1000;
    limiteDepositoDiario = 3;
  }

  public void depositar(double cuanto) {
    validarMonto(cuanto);
    validarLimiteDepositoDiario(limiteDepositoDiario);
    agregarMovimiento(new Deposito(LocalDate.now(), cuanto));
  }

  public void extraer(double cuanto) {
    validarMonto(cuanto);
    validarSaldoDisponible(cuanto);
    validarLimiteExtraccionDiario(cuanto);
    agregarMovimiento(new Extraccion(LocalDate.now(), cuanto));
  }

  public void agregarMovimiento( Movimiento movimiento) {
    movimientos.add(movimiento);
    setSaldo(movimiento.calcularValor(this));
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
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

  private void validarSaldoDisponible(double cuanto) {
    if ( getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  private void validarLimiteExtraccionDiario(double cuanto) {
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = limiteExtraccionDiario - montoExtraidoHoy;
    if ( cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + limiteExtraccionDiario
          + " diarios, límite: " + limite);
    }
  }

  private void validarMonto(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private void validarLimiteDepositoDiario(Integer limiteDepositoDiario) {
    if (getMovimientos().stream().filter(Movimiento::isDeposito).count() >= limiteDepositoDiario) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + limiteDepositoDiario + " depositos diarios");
    }
  }
}
