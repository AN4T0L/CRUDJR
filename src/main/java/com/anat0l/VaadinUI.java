package com.anat0l;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;

import static com.vaadin.ui.Notification.TYPE_WARNING_MESSAGE;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

    @Autowired
    private PersonService service;
    private Person person;
    private List<Person> persons = new ArrayList<Person>();
    private Grid grid = new Grid();

    // Create a text field
    private TextField tfName = new TextField("Name");
    private TextField tfAge = new TextField("Age");
    private TextField tfId = new TextField("Id");
    private TextField tfAdmin = new TextField("Admin");
    private Button btUpdate = new Button("Update by id");
    private Button btFind = new Button("Find by id");
    private Button btInsert = new Button("Insert");
    private Button btDelete = new Button("Delete");

    // Put some initial content in it
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        updateGrid();
        grid.setColumns("id","name", "age", "admin","createdDate");
        grid.addSelectionListener(e -> updateForm());
        btUpdate.addClickListener(e -> updatePerson());
        btFind.addClickListener(e-> findPerson());
        btInsert.addClickListener(e -> insertPerson());
        btDelete.addClickListener(e -> removePerson());
        //Make layouts page
        Label crudLabel = new Label("CRUD");
        crudLabel.setSizeFull();
        VerticalLayout mainLayout = new VerticalLayout(crudLabel);
        HorizontalLayout tableLayout = new HorizontalLayout(grid);
        VerticalLayout fieldLayout = new VerticalLayout(tfId,tfName,tfAge,tfAdmin);
        HorizontalLayout buttonLayout = new HorizontalLayout(btInsert, btUpdate, btDelete, btFind);
        //Structurized Layouts on page
        tableLayout.setMargin(true);
        tableLayout.setSpacing(true);
        tableLayout.addComponent(fieldLayout);
        fieldLayout.addComponent(buttonLayout);
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
        mainLayout.addComponent(tableLayout);
        setContent(mainLayout);
    }

    //Обновление Списка лиц
    private void updateGrid() {
        //Создание списка персон
        persons = service.getAllPerson();
        grid.setContainerDataSource(new BeanItemContainer<>(Person.class,
                persons));
    }

    //Обновление имени или возраста или статуса админа
    private void updatePerson() {
        try {
            Long id = Long.parseLong(tfId.getValue());
            person = new Person();
            person.setId(id);
            Person oldPers = service.getPersonById((int)(long)id);
            if(tfName.isEmpty()){person.setName(oldPers.getName());}
            else{person.setName(tfName.getValue());
            }
            if(tfAge.isEmpty() ){person.setAge(oldPers.getAge());}
            else{person.setAge(Integer.parseInt(tfAge.getValue()));}
            if(tfAdmin.isEmpty())person.setAdmin(oldPers.getAdmin());
            else {if(Integer.parseInt(tfAdmin.getValue())!=0 ||Integer.parseInt(tfAdmin.getValue())!=1)
                    throw new NumberFormatException();
                  else person.setAdmin(Integer.parseInt(tfAdmin.getValue()));}
            service.updatePerson(person);
            Notification n = new Notification("UPDATED", "Name: " + person.getName()
                    + "\nCareer: " + person.getAge()+ "\nAdmin: " + person.getAdmin()
                    + "\nDate: "+ person.getCreatedDate().toString(),
                    Notification.Type.TRAY_NOTIFICATION);
            n.setDelayMsec(3000);
            n.setPosition(Position.TOP_CENTER);
            n.show(Page.getCurrent());
        }catch (NumberFormatException e){
            Notification.show("WARNING","Insert 0 or 1 in Admin form", TYPE_WARNING_MESSAGE);
        }
        catch (Exception e){
            Notification.show("WARNING","Not valid data",
                    TYPE_WARNING_MESSAGE);
        }
        updateGrid();
    }

    //Добавление нового лица в бд
    private void insertPerson() {
        tfId.setValue("");
        try {
            Integer i = Integer.parseInt(tfAdmin.getValue());
            if(tfName.isEmpty() || tfAge.isEmpty() || tfAdmin.isEmpty()) throw new Exception();
            if (isNumeric(tfName.getValue())) {
                Notification.show("WARNING", "Insert String value in Name form, please ",
                        TYPE_WARNING_MESSAGE);
            } else if (!isNumeric(tfAge.getValue())) {
                Notification.show("WARNING", "Insert Integer value in Age form, please ",
                        TYPE_WARNING_MESSAGE);
                tfAge.setValue("");
            }else if (!isNumeric(tfAdmin.getValue()) || i<0 || i>1) {
                Notification.show("WARNING", "Insert 0(false) or 1(true) value in Admin form, please ",
                        TYPE_WARNING_MESSAGE);
            } else {
                String name = tfName.getValue();
                Integer age = Integer.parseInt(tfAge.getValue());
                Integer admin = Integer.parseInt(tfAdmin.getValue());
                person = new Person(name, age, admin);
                service.insertPerson(person);
                Notification n = new Notification("INSERTED", "Name: " + name + "\nAge: " + age, Notification.Type.TRAY_NOTIFICATION);
                n.setDelayMsec(3000);
                n.setPosition(Position.TOP_CENTER);
                n.show(Page.getCurrent());
            }
        }
        catch (Exception e){
            Notification.show("WARNING","Insert values in forms",
                    TYPE_WARNING_MESSAGE);
        }
        updateGrid();
    }

    //удаление лица
    private void removePerson() {
        tfName.setValue("");
        tfAge.setValue("");
        tfAdmin.setValue("");
        try{
        Integer id = Integer.parseInt(tfId.getValue());
        service.removePersonById(id);
            Notification n = new Notification("REMOVED", "Person id: " + id, Notification.Type.TRAY_NOTIFICATION);
            n.setDelayMsec(5000);
            n.setPosition(Position.TOP_CENTER);
            n.show(Page.getCurrent());}
        catch (Exception e){
            Notification.show("WARNING", "Insert int value in id form, please ",
                    TYPE_WARNING_MESSAGE);
        }
        updateGrid();
    }

    //Поиск лица в бд и вывод запись данных в формы
    private void findPerson() {
            tfName.setValue("");
            tfAge.setValue("");
            tfAdmin.setValue("");
            try {
                Integer id = Integer.parseInt(tfId.getValue());
                Person person = service.getPersonById(id);
                tfName.setValue(person.getName());
                tfId.setValue(person.getId().toString());
                tfAge.setValue(person.getAge()+"");
                tfAdmin.setValue(Integer.toString(person.getAdmin()));
                Notification n = new Notification("Pushed in forms", Notification.Type.TRAY_NOTIFICATION);
                n.setDelayMsec(3000);
                n.setPosition(Position.TOP_CENTER);
                n.show(Page.getCurrent());
            } catch (Exception e) {
                Notification.show("WARNING", "Insert int value in id form, please ",
                        TYPE_WARNING_MESSAGE);
            }
    }
    private void updateForm() {
        if (grid.getSelectedRows().isEmpty()) {
        } else {
            person = (Person) grid.getSelectedRow();
            tfId.setValue(person.getId().toString());
            tfName.setValue(person.getName());
            tfAge.setValue(person.getAge()+"");
            tfAdmin.setValue(Integer.toString(person.getAdmin()));

        }
    }

    private boolean isNumeric(String str)
    {
        try{
            double d = Double.parseDouble(str);
        }catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}