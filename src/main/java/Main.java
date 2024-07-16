import de.milchreis.uibooster.UiBooster;
import de.milchreis.uibooster.model.Form;
import de.milchreis.uibooster.model.FormElement;
import de.sbs.fswi1.models.StudentDTO;
import de.sbs.fswi1.services.DataAccessObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Main {

  private static DataAccessObject dao;

  public static void main(String[] args) {
    dao = new DataAccessObject("http://192.168.4.247:8080/studenten");
    System.out.println("Studentenverwaltung");
    System.out.println("a = Studenten hinzufügen");
    System.out.println("d = Studenten löschen");
    System.out.println("c = Studenten ändern");
    System.out.println("x = Programm beenden");

    boolean runner = true;
    while (runner) {
      String textInput = new UiBooster().showTextInputDialog("Befehl?");
      if ("x".equalsIgnoreCase(textInput)) {
        System.out.println("Auf wiedersehen.");
        System.exit(0);
        runner = false;
      } else if ("a".equalsIgnoreCase(textInput)) {
        addStudent();
      } else if ("d".equalsIgnoreCase(textInput)) {
        deleteStudent();
      } else if ("c".equalsIgnoreCase(textInput)) {
        changeStudent();
      }
    }
  }

  private static void changeStudent() {
    String textInput = new UiBooster().showTextInputDialog("Welche Id soll verändert werden?");
    try {
      long buf = Long.parseLong(textInput);
      StudentDTO studentFromDB = findStudentById(buf);
      if (studentFromDB != null) {
        String selection =
            new UiBooster()
                .showSelectionDialog(
                    "Werte ändern",
                    "Welcher Wert soll geändert werden?",
                    Arrays.asList("Vorname", "Nachname", "Geburtsdatum", "Klasse"));

        Date birthday;
        switch (selection) {
          case "Vorname":
            textInput = new UiBooster().showTextInputDialog("Neuer Vorname?");
            if (textInput != null && !textInput.isEmpty()) {
              dao.update(
                  buf,
                  new StudentDTO(
                      textInput,
                      studentFromDB.getNachname(),
                      studentFromDB.getGeburtsdatum(),
                      studentFromDB.getKlasse()));
            }
            break;
          case "Nachname":
            textInput = new UiBooster().showTextInputDialog("Neuer Nachname?");
            if (textInput != null && !textInput.isEmpty()) {
              dao.update(
                  buf,
                  new StudentDTO(
                      studentFromDB.getVorname(),
                      textInput,
                      studentFromDB.getGeburtsdatum(),
                      studentFromDB.getKlasse()));
            }
            break;
          case "Geburtsdatum":
            birthday = new UiBooster().showDatePicker("Neuer Geburtstag", "Geburtsdatum");
            if (birthday != null) {
              SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
              String formattedDate = formatter.format(birthday);
              dao.update(
                  buf,
                  new StudentDTO(
                      studentFromDB.getVorname(),
                      studentFromDB.getNachname(),
                      formattedDate,
                      studentFromDB.getKlasse()));
            }
            break;
          case "Klasse":
            textInput = new UiBooster().showTextInputDialog("Neue Klasse?");
            if (textInput != null && !textInput.isEmpty()) {
              dao.update(
                  buf,
                  new StudentDTO(
                      studentFromDB.getVorname(),
                      studentFromDB.getNachname(),
                      studentFromDB.getGeburtsdatum(),
                      textInput.toUpperCase()));
            }
            break;
        }
      }
    } catch (Exception ignored) {
    }
  }

  private static StudentDTO findStudentById(long buf) {
    for (var item : dao.findAll()) {
      if (item.getId() == buf) {
        return item;
      }
    }
    return null;
  }

  private static void deleteStudent() {
    String textInput = new UiBooster().showTextInputDialog("Welche Id soll gelöscht werden?");
    try {
      long buf = Long.parseLong(textInput);
      dao.delete(buf);
    } catch (Exception ignored) {
    }
  }

  private static void addStudent() {
    Form form;
    while (true) {
      form =
          new UiBooster()
              .createForm("Studenten anlegen")
              .addText("Vorname?")
              .addText("Nachname?")
              .addDatePicker("Datum?")
              .addText("Klasse?")
              .show();

      if (!form.getByLabel("Vorname?").asString().isBlank()
          || !form.getByLabel("Nachname?").asString().isBlank()
          || !form.getByLabel("Klasse?").asString().isBlank()) break;
    }

    FormElement<?> stringVorname = form.getByLabel("Vorname?");
    FormElement<?> stringNachname = form.getByLabel("Nachname?");
    FormElement<?> stringDatum = form.getByLabel("Datum?");
    FormElement<?> stringKlasse = form.getByLabel("Klasse?");

    String datum =
        stringDatum.asDateFormatted().substring(8, 10)
            + "."
            + stringDatum.asDateFormatted().substring(5, 7)
            + "."
            + stringDatum.asDateFormatted().substring(0, 4);

    dao.save(
        new StudentDTO(
            stringVorname.asString(), stringNachname.asString(), datum, stringKlasse.asString()));
  }
}
