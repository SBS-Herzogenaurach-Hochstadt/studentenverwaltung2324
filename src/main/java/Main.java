import de.sbs.fswi1.services.DataAccessObject;

public class Main {

    private static DataAccessObject dao;
    public static void main(String[] args) {
        dao = new DataAccessObject("http://localhost:8080/studenten");
        System.out.println(dao.findAll());
    }
}
