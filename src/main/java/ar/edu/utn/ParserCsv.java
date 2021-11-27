package ar.edu.utn;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Path;

public class ParserCsv {
    private String comment;
    private List<String> columns;
    private List<String> outputColumns;
    private List<String> lineasErroneas;
    private String filterValue;
    private String filterColumn;
    private String separator;

    public ParserCsv(String comment, List<String> columns, String filterValue, String filterColumn,
                     List<String> outputColumns, String separator) {
        this.comment = comment;
        this.columns = columns;
        this.filterValue = filterValue;
        this.filterColumn = filterColumn;
        this.outputColumns = outputColumns;
        this.separator = separator;
        this.lineasErroneas = new ArrayList<>();
        if (!this.columns.contains(this.filterColumn )){
            throw  new IllegalStateException("El filtro elegido no esta dentro de la lista de columnas");
        }
    }

    public ParserCsv( List<String> columns,
                     List<String> outputColumns) {
        this.comment = "#";
        this.columns = columns;
        this.filterValue = null;
        this.filterColumn = null;
        this.outputColumns = outputColumns;
        this.separator = ",";
    }
    public ParserCsv( List<String> outputColumns) {
        this.comment = "#";
        this.columns = null;
        this.filterValue = null;
        this.filterColumn = null;
        this.outputColumns = outputColumns;
        this.separator = ",";
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getFilterColumn() {
        return filterColumn;
    }



    public List<String> getOutputColumns() {
        return outputColumns;
    }

    public void setOutputColumns(List<String> outputColumns) {
        this.outputColumns = outputColumns;
    }

    public List<String> getLineasErroneas() {
        return lineasErroneas;
    }

    public void setLineasErroneas(List<String> lineasErroneas) {
        this.lineasErroneas = lineasErroneas;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void parse(BufferedReader br, Path output) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(output)) {
            String line = null;
            while ((line = br.readLine()) != null) {

                List<String> registro = null;
                try {
                    registro = this.parseLine(line);
                } catch (LineaInvalidaException e) {
                    this.lineasErroneas.add(line);
                }
                if (registro != null) {
                    bw.write(String.join(this.separator, registro));
                    bw.newLine();
                }
            }
        }
    }

    public List<String> parseLine(String linea) throws LineaInvalidaException {
        List<String> output = null;
        if ( ! linea.trim().startsWith( this.comment )       ) {         // no comentario
            String[] lineaArr = linea.split(this.separator);
            if(lineaArr.length == this.columns.size()){  // cantidad de lineas
                int nroColFiltro = this.columns.indexOf(this.filterColumn) ;
                if (lineaArr[nroColFiltro].equals(this.filterValue)){ // coincida el filtro
                    output = new ArrayList<>();
                    for (int i = 0; i < this.columns.size(); i++) {
                        String column = this.columns.get(i);
                        if (this.outputColumns.contains(column)) {
                            String value = lineaArr[i];
                            output.add(value);
                        }
                    }
                }

            } else {
                throw new LineaInvalidaException(linea);
            }
        }


        return output;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Path path = Paths.get("midata.csv");
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)){
            ParserCsv parser = new ParserCsv("#",
                    Arrays.asList(new String[]{"nombre", "edad", "profesion"}),
                    "programador", "profesion",
                    Arrays.asList(new String[]{"nombre",  "profesion"}), ",");
            parser.parse(bufferedReader, Paths.get("salida_midata.csv"));
        }

    }

//    public static void main(String[] args) throws IOException, URISyntaxException {
//
//
//        ArgumentParser parser = ArgumentParsers.newFor("csvParser").build()
//                .description("selecciona de un csv");
//        parser.addArgument("--cols_in")
//                .type(String.class).required(true)
//                .help("aColumnas de entrada");
//        parser.addArgument("--cols_out")
//                .type(String.class).required(true)
//                .help("Columnas de salida");
//
//        parser.addArgument("--archivo_in")
//                .help("archivo de entrada").required(true);
//        parser.addArgument("--archivo_out")
//                .help("archivo de salida").required(true);
//
//
//        try {
//            Namespace res = parser.parseArgs(args);
//            Path path = Paths.get(res.get("archivo_in").toString());
//
//            try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
//                ParserCsv parsercsv = new ParserCsv("#",
//                        Arrays.asList(res.get("cols_in").toString().split(",")),
//                        "programador", "profesion",
//                        Arrays.asList(res.get("cols_out").toString().split(",")), ",");
//                parsercsv.parse(bufferedReader, Paths.get(res.get("archivo_out").toString()));
//            }
//
//        } catch (ArgumentParserException e) {
//            parser.handleError(e);
//        }
//
//
//    }

}
