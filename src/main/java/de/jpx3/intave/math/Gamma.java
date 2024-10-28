package de.jpx3.intave.math;

// This class is a copy of the smile.math.special.Gamma class from the Smile library.
public final class Gamma {
  private static final double FPMIN = 1.0E-300;
  private static final int LANCZOS_N = 6;
  private static final double[] LANCZOS_COEFF = new double[]{1.000000000190015, 76.18009172947146, -86.50532032941678, 24.01409824083091, -1.231739572450155, 0.001208650973866179, -5.395239384953E-6};
  private static final double LANCZOS_SMALL_GAMMA = 5.0;
  private static final int INCOMPLETE_GAMMA_MAX_ITERATIONS = 1000;
  private static final double INCOMPLETE_GAMMA_EPSILON = 1.0E-8;

  private Gamma() {
  }

  public static double gamma(double x) {
    double xcopy = x;
    double first = x + 5.0 + 0.5;
    double second = LANCZOS_COEFF[0];
    double fg = 0.0;
    if (x >= 0.0) {
      if (x >= 1.0 && x - (double)((int)x) == 0.0) {
        fg = factorial((int)x - 1);
      } else {
        first = Math.pow(first, x + 0.5) * Math.exp(-first);

        for(int i = 1; i <= 6; ++i) {
          second += LANCZOS_COEFF[i] / ++xcopy;
        }

        fg = first * Math.sqrt(6.283185307179586) * second / x;
      }
    } else {
      fg = -3.141592653589793 / (x * gamma(-x) * Math.sin(Math.PI * x));
    }

    return fg;
  }

  public static double lgamma(double x) {
    double xcopy = x;
    double fg = 0.0;
    double first = x + 5.0 + 0.5;
    double second = LANCZOS_COEFF[0];
    if (x >= 0.0) {
      if (x >= 1.0 && x - (double)((int)x) == 0.0) {
        fg = lfactorial((int)x - 1);
      } else {
        first -= (x + 0.5) * Math.log(first);

        for(int i = 1; i <= 6; ++i) {
          second += LANCZOS_COEFF[i] / ++xcopy;
        }

        fg = Math.log(Math.sqrt(6.283185307179586) * second / x) - first;
      }
    } else {
      fg = Math.PI / (gamma(1.0 - x) * Math.sin(Math.PI * x));
      if (Double.isFinite(fg)) {
        if (fg < 0.0) {
          throw new IllegalArgumentException("The gamma function is negative: " + fg);
        }

        fg = Math.log(fg);
      }
    }
    return fg;
  }

  public static double lfactorial(int n) {
    if (n < 0) {
      throw new IllegalArgumentException(String.format("n has to be non-negative: %d", n));
    } else {
      double f = 0.0;

      for(int i = 2; i <= n; ++i) {
        f += Math.log((double)i);
      }

      return f;
    }
  }

  public static double factorial(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("n has to be non-negative.");
    } else {
      double f = 1.0;
      for(int i = 2; i <= n; ++i) {
        f *= i;
      }
      return f;
    }
  }

  public static double regularizedIncompleteGamma(double s, double x) {
    if (s < 0.0) {
      throw new IllegalArgumentException("Invalid s: " + s);
    } else if (x < 0.0) {
      throw new IllegalArgumentException("Invalid x: " + x);
    } else {
      double igf = 0.0;
      if (x < s + 1.0) {
        igf = regularizedIncompleteGammaSeries(s, x);
      } else {
        igf = regularizedIncompleteGammaFraction(s, x);
      }

      return igf;
    }
  }

  public static double regularizedUpperIncompleteGamma(double s, double x) {
    if (s < 0.0) {
      throw new IllegalArgumentException("Invalid s: " + s);
    } else if (x < 0.0) {
      throw new IllegalArgumentException("Invalid x: " + x);
    } else {
      double igf = 0.0;
      if (x != 0.0) {
        if (Double.isNaN(x)) {
          igf = 1.0;
        } else if (x < s + 1.0) {
          igf = 1.0 - regularizedIncompleteGammaSeries(s, x);
        } else {
          igf = 1.0 - regularizedIncompleteGammaFraction(s, x);
        }
      }

      return igf;
    }
  }

  private static double regularizedIncompleteGammaSeries(double a, double x) {
    if (!(a < 0.0) && !(x < 0.0) && !(x >= a + 1.0)) {
      int i = 0;
      double igf = 0.0;
      boolean check = true;
      double acopy = a;
      double sum = 1.0 / a;
      double incr = sum;
      double loggamma = lgamma(a);

      while(check) {
        ++i;
        ++a;
        incr *= x / a;
        sum += incr;
        if (Math.abs(incr) < Math.abs(sum) * 1.0E-8) {
          igf = sum * Math.exp(-x + acopy * Math.log(x) - loggamma);
          check = false;
        }

        if (i >= 1000) {
          check = false;
          igf = sum * Math.exp(-x + acopy * Math.log(x) - loggamma);
        }
      }

      return igf;
    } else {
      throw new IllegalArgumentException(String.format("Invalid a = %f, x = %f", a, x));
    }
  }

  private static double regularizedIncompleteGammaFraction(double a, double x) {
    if (!(a < 0.0) && !(x < 0.0) && !(x < a + 1.0)) {
      int i = 0;
      double ii = 0.0;
      double igf = 0.0;
      boolean check = true;
      double loggamma = lgamma(a);
      double numer = 0.0;
      double incr = 0.0;
      double denom = x - a + 1.0;
      double first = 1.0 / denom;
      double term = 9.999999999999999E299;
      double prod = first;

      while(check) {
        ++i;
        ii = (double)i;
        numer = -ii * (ii - a);
        denom += 2.0;
        first = numer * first + denom;
        if (Math.abs(first) < 1.0E-300) {
          first = 1.0E-300;
        }

        term = denom + numer / term;
        if (Math.abs(term) < 1.0E-300) {
          term = 1.0E-300;
        }

        first = 1.0 / first;
        incr = first * term;
        prod *= incr;
        if (Math.abs(incr - 1.0) < 1.0E-8) {
          check = false;
        }

        if (i >= 1000) {
          check = false;
        }
      }

      igf = 1.0 - Math.exp(-x + a * Math.log(x) - loggamma) * prod;
      return igf;
    } else {
      throw new IllegalArgumentException(String.format("Invalid a = %f, x = %f", a, x));
    }
  }

  public static double digamma(double x) {
    double[][] C7 = new double[][]{{13524.999667726346, 45285.60169954729, 45135.168469736665, 18529.01181858261, 3329.1525149406934, 240.68032474357202, 5.157789200013909, 0.006228350691898475}, {6.938911175376345E-7, 19768.574263046736, 41255.16083535383, 29390.287119932684, 9081.966607485518, 1244.7477785670856, 67.4291295163786, 1.0}};
    double[][] C4 = new double[][]{{-2.7281757513152966E-15, -0.6481571237661965, -4.486165439180193, -7.016772277667586, -2.1294044513101054}, {7.777885485229616, 54.61177381032151, 89.29207004818613, 32.270349379114336, 1.0}};
    double prodPj = 0.0;
    double prodQj = 0.0;
    double digX = 0.0;
    double x2;
    int j;
    if (x >= 3.0) {
      x2 = 1.0 / (x * x);

      for(j = 4; j >= 0; --j) {
        prodPj = prodPj * x2 + C4[0][j];
        prodQj = prodQj * x2 + C4[1][j];
      }

      digX = Math.log(x) - 0.5 / x + prodPj / prodQj;
    } else if (x >= 0.5) {
      x2 = 1.4616321449683622;
      for(j = 7; j >= 0; --j) {
        prodPj = x * prodPj + C7[0][j];
        prodQj = x * prodQj + C7[1][j];
      }
      digX = (x - 1.4616321449683622) * (prodPj / prodQj);
    } else {
      x2 = 1.0 - x - Math.floor(1.0 - x);
      digX = digamma(1.0 - x) + Math.PI / Math.tan(Math.PI * x2);
    }

    return digX;
  }

  public static double inverseRegularizedIncompleteGamma(double a, double p) {
    if (a <= 0.0) {
      throw new IllegalArgumentException("a must be pos in invgammap");
    } else {
      double EPS = 1.0E-8;
      double lna1 = 0.0;
      double afac = 0.0;
      double a1 = a - 1.0;
      double gln = lgamma(a);
      if (p >= 1.0) {
        return Math.max(100.0, a + 100.0 * Math.sqrt(a));
      } else if (p <= 0.0) {
        return 0.0;
      } else {
        double x;
        double t;
        if (a > 1.0) {
          lna1 = Math.log(a1);
          afac = Math.exp(a1 * (lna1 - 1.0) - gln);
          double pp = p < 0.5 ? p : 1.0 - p;
          t = Math.sqrt(-2.0 * Math.log(pp));
          x = (2.30753 + t * 0.27061) / (1.0 + t * (0.99229 + t * 0.04481)) - t;
          if (p < 0.5) {
            x = -x;
          }

          x = Math.max(0.001, a * Math.pow(1.0 - 1.0 / (9.0 * a) - x / (3.0 * Math.sqrt(a)), 3.0));
        } else {
          t = 1.0 - a * (0.253 + a * 0.12);
          if (p < t) {
            x = Math.pow(p / t, 1.0 / a);
          } else {
            x = 1.0 - Math.log(1.0 - (p - t) / (1.0 - t));
          }
        }

        int j = 0;

        while(true) {
          if (j < 12) {
            if (x <= 0.0) {
              return 0.0;
            }
            double err = regularizedIncompleteGamma(a, x) - p;
            if (a > 1.0) {
              t = afac * Math.exp(-(x - a1) + a1 * (Math.log(x) - lna1));
            } else {
              t = Math.exp(-x + a1 * Math.log(x) - gln);
            }

            double u = err / t;
            x -= t = u / (1.0 - 0.5 * Math.min(1.0, u * ((a - 1.0) / x - 1.0)));
            if (x <= 0.0) {
              x = 0.5 * (x + t);
            }
            if (!(Math.abs(t) < 1.0E-8 * x)) {
              ++j;
              continue;
            }
          }
          return x;
        }
      }
    }
  }
}
