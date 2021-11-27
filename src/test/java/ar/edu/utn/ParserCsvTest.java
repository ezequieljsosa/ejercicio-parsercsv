package ar.edu.utn;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ParserCsvTest {

    private ParserCsv parser;

    @Before
    public void setUp() {
        List<String> columns = Arrays.asList(new String[]{"nombre", "edad", "profesion"});
        List<String> outputColumns = Arrays.asList(new String[]{"nombre", "edad", "profesion"});
        parser = new ParserCsv("#", columns,
                "programador", "profesion",
                outputColumns, ",");
    }

    @Test
    public void testLineaOk() throws Exception {
        String linea = "ezequiel,36,programador";
        List<String> registro = parser.parseLine(linea);
        Assert.assertArrayEquals(
                new Object[]{"ezequiel", "36", "programador"},
                registro.toArray());
    }

    @Test
    public void testLineaComentario() throws Exception {
        String linea = "#ezequiel,36,programador";
        List<String> registro = parser.parseLine(linea);
        Assert.assertNull("Si arranca con un comentario no deberia generar un registro", registro);
    }

    @Test(expected = LineaInvalidaException.class)
    public void testLineaColumnasInsuficientes() throws Exception {
        String linea = "ezequiel,36";
        parser.parseLine(linea);
    }

    @Test
    public void testLineaNofiltrada() throws Exception {
        String linea = "ezequiel,36,carpintero";
        List<String> registro = parser.parseLine(linea);
        Assert.assertNull( "se deberia haber filtrado por profesion" , registro);
    }

    @Test
    public void testLineaSinFiltro() throws Exception {
        String linea = "ezequiel,36,carpintero";
        List<String> columns = Arrays.asList(new String[]{"nombre", "edad", "profesion"});
        List<String> outputColumns = Arrays.asList(new String[]{"nombre", "edad", "profesion"});
        parser = new ParserCsv("#", columns,
                "programador", "zaraza",
                outputColumns, ",");
        List<String> registro = parser.parseLine(linea);
        Assert.assertNull( "se deberia haber filtrado por profesion" , registro);
    }


}
