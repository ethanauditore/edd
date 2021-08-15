package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {}

    /**
     * Función de dispersión XOR.
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int n = llave.length;
        if ((n % 4) != 0) {
            byte[] arreglo = new byte[n + (4 - (n % 4))];
            for (int i = 0; i < llave.length; i++)
                arreglo[i] = llave[i];
            llave = arreglo;
            n = llave.length;
        }
        int r = 0;
        for (int i = 0; i + 3 < n; i += 4)
            r ^= bigEndian(llave[i], llave[i + 1], llave[i + 2], llave[i + 3]);
        return r;
    }

    private static int bigEndian(byte a, byte b, byte c, byte d) {
        return (((a & 0xFF) << 24) | ((b & 0xFF) << 16) |
                ((c & 0xFF) << 8) | ((d & 0xFF)));
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int a = 0x9E3779B9, b = a, c = 0xFFFFFFFF;
        int i = 0, n = llave.length, bytes = n;
        for (i = 0; i + 11 < llave.length; i += 12) {
            a += littleEndian(llave[i],     llave[i + 1], llave[i + 2],  llave[i + 3]);
            b += littleEndian(llave[i + 4], llave[i + 5], llave[i + 6],  llave[i + 7]);
            c += littleEndian(llave[i + 8], llave[i + 9], llave[i + 10], llave[i + 11]);
            int[] r = mezcla(a, b, c);
            a = r[0]; b = r[1]; c = r[2];
            bytes -= 12;
        }
        c += n;
        switch (bytes) {
            case 11: c += ((llave[i + 10] & 0xFF) << 24);
            case 10: c += ((llave[i + 9] & 0xFF) << 16);
            case 9:  c += ((llave[i + 8] & 0xFF) << 8);
            case 8:  b += ((llave[i + 7] & 0xFF) << 24);
            case 7:  b += ((llave[i + 6] & 0xFF) << 16);
            case 6:  b += ((llave[i + 5] & 0xFF) << 8);
            case 5:  b += (llave[i + 4] & 0xFF);
            case 4:  a += ((llave[i + 3] & 0xFF) << 24);
            case 3:  a += ((llave[i + 2] & 0xFF) << 16);
            case 2:  a += ((llave[i + 1] & 0xFF) << 8);
            case 1:  a += (llave[i] & 0xFF);
        }

        return mezcla(a, b, c)[2];
    }

    private static int littleEndian(byte a, byte b, byte c, byte d) {
        return (((d & 0xFF) << 24) | ((c & 0xFF) << 16) |
                ((b & 0xFF) << 8) | ((a & 0xFF)));
    }

    private static int[] mezcla(int a, int b, int c) {
        a -= b; a -= c; a ^= (c >>> 13);
        b -= c; b -= a; b ^= (a << 8);
        c -= a; c -= b; c ^= (b >>> 13);
        a -= b; a -= c; a ^= (c >>> 12);
        b -= c; b -= a; b ^= (a << 16);
        c -= a; c -= b; c ^= (b >>> 5);
        a -= b; a -= c; a ^= (c >>> 3);
        b -= c; b -= a; b ^= (a << 10);
        c -= a; c -= b; c ^= (b >>> 15);
        return new int[]{a, b, c};
    }

    /**
     * Función de dispersión Daniel J. Bernstein.
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
        int h = 5381;
        for (int i = 0; i < llave.length; i++)
            h += (h << 5) + (llave[i] & 0xFF);
        return h;
    }
}
