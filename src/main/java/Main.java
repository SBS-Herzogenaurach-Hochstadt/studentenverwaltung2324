import de.sbs.fswi1.models.StudentDTO;
import de.sbs.fswi1.services.DataAccessObject;

public class Main {

    private static DataAccessObject dao;

    public static void main(String[] args) {
        dao = new DataAccessObject("http://localhost:8080/studenten");
        StudentDTO student = new StudentDTO("Reiner", "Unsinn", "12.12.2002", "FSWI-2");
        dao.save(student);

        System.out.println(dao.findAll());
    }
}
