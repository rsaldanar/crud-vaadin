package com.example.application.views.personform;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Person Form")
@Route(value = "person-form", layout = MainLayout.class)
@Uses(Icon.class)
public class PersonFormView extends Composite<VerticalLayout> {

    // Creamos el layout principal de la vista
    VerticalLayout layoutColumn2 = new VerticalLayout();
    H3 h3 = new H3("Información Personal");
    // Creamos un layout para los campos del formulario
    FormLayout formLayout2Col = new FormLayout();
    // Creamos un layout para los botones de guardar y cancelar
    HorizontalLayout layoutRow = new HorizontalLayout();

    // Aqui definimos las constantes que usaremos en el formulario
    private static final String BUTTON_SAVE_TEXT = "Save";
    private static final String BUTTON_UPDATE_TEXT = "Update";

    // Inyectamos el servicio que usaremos para guardar y listar personas
    private final SamplePersonService samplePersonService;

    // Definimos los componentes que usaremos en el formulario desde Vaadin flow
    private final TextField nameTextField = new TextField("Nombre");
    private final TextField lastNameTextField = new TextField("Apellido");
    private final TextField ageTextField = new TextField("Edad");
    private final Grid<SamplePerson> basicGrid = new Grid<>(SamplePerson.class);

    private final Button buttonSave = new Button(BUTTON_SAVE_TEXT);
    private final Button buttonCancel = new Button("Cancel");

    // Constructor de la vista, inyectamos el servicio que usaremos
    public PersonFormView(@Autowired SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        initializeView();
        //<theme-editor-local-classname>
        addClassName("person-form-view-vertical-layout-1");
    }

    /**
     * Inicializamos la vista con los componentes que usaremos,
     * configuramos los listeners de los botones,
     * y configuramos el grid con los datos de la base de datos.
     */
    private void initializeView() {
        // Configuramos los componentes
        configureComponents();

        setGridSampleData();

        // Configuramos los listeners de los botones
        setButtonClickListeners();
    }

    private void configureComponents() {
        // Configuramos los estilos y tamaños de los componentes
        getContent().setWidth("100%");
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");

        h3.setWidth("100%");

        formLayout2Col.setWidth("100%");

        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        // Configuramos los botones
        buttonSave.setWidth("min-content");
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonCancel.setWidth("min-content");

        getBasicGrid();

        // Agregamos los componentes al layout principal
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3, formLayout2Col, layoutRow);
        // Agregamos los componentes al layout del formulario
        formLayout2Col.add(nameTextField, lastNameTextField, ageTextField);
        // Agregamos los botones al layout de botones
        layoutRow.add(buttonSave, buttonCancel);
        // Agregamos el grid al layout principal
        getContent().add(basicGrid);
    }

    public Grid getBasicGrid() {
        // Configuramos el grid
        basicGrid.setWidth("100%");
        basicGrid.getStyle().set("flex-grow", "0");
//        basicGrid.setColumns("id", "firstName", "lastName", "age", "version");
//        basicGrid.getColumnByKey("id").setVisible(false);
////        basicGrid.getColumnByKey("version").setVisible(false);
//        basicGrid.getColumnByKey("firstName").setHeader(LineAwesomeIcon.USER.create());

        // Configuramos el grid para que al seleccionar un registro se llene el formulario
        basicGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                fillFormWithData(event.getValue());
                nameTextField.focus();
            }
        });
        return basicGrid;
    }

    /**
     * Configuramos los listeners de los botones
     * y el grid para que se actualice al seleccionar un registro
     * y se pueda editar.
     */
    private void setButtonClickListeners() {
        buttonCancel.addClickListener(e -> {
            cleanForm();
            basicGrid.asSingleSelect().clear();
            buttonSave.setText(BUTTON_SAVE_TEXT);
        });

        buttonSave.addClickListener(e -> {
            if (buttonSave.getText().equals(BUTTON_UPDATE_TEXT)) {
                updatePerson();
            } else {
                nameTextField.focus();
                savePerson();
            }
        });

    }

    /**
     * Guardamos la persona en la base de datos
     * y actualizamos el grid con los datos de la base de datos.
     */
    private void savePerson() {
        if (validateFields()) {
            SamplePerson samplePerson = createSamplePersonFromFields();
            samplePersonService.save(samplePerson);
            cleanForm();
            setGridSampleData();
            nameTextField.focus();
        }
    }

    /**
     * Actualizamos la persona en la base de datos
     * y actualizamos el grid con los datos de la base de datos.
     */
    private void updatePerson() {
        SamplePerson samplePerson = basicGrid.asSingleSelect().getValue();
        samplePerson.setFirstName(nameTextField.getValue());
        samplePerson.setLastName(lastNameTextField.getValue());
        samplePerson.setAge(Integer.parseInt(ageTextField.getValue()));
        samplePersonService.save(samplePerson);
        cleanForm();
        setGridSampleData();
        buttonSave.setText(BUTTON_SAVE_TEXT);
    }

    /**
     * Llenamos el formulario con los datos de la persona seleccionada
     * para poder editarla.
     */
    private void fillFormWithData(SamplePerson person) {
        nameTextField.setValue(person.getFirstName());
        lastNameTextField.setValue(person.getLastName());
        ageTextField.setValue(String.valueOf(person.getAge()));
        buttonSave.setText(BUTTON_UPDATE_TEXT);
    }

    /**
     * Creamos un objeto SamplePerson con los datos del formulario
     * para poder guardarlo en la base de datos.
     */
    private SamplePerson createSamplePersonFromFields() {
        SamplePerson samplePerson = new SamplePerson();
        samplePerson.setFirstName(nameTextField.getValue());
        samplePerson.setLastName(lastNameTextField.getValue());
        samplePerson.setAge(Integer.parseInt(ageTextField.getValue()));
        return samplePerson;
    }

    /**
     * Validamos que los campos del formulario no estén vacíos
     * y mostramos una notificación si es necesario.
     */
    private boolean validateFields() {
        boolean isValid = true;

        if (nameTextField.isEmpty() || lastNameTextField.isEmpty() || ageTextField.isEmpty()) {
            Notification.show("Todos los campos son requeridos para guardar");
            nameTextField.setInvalid(true);
            lastNameTextField.setInvalid(true);
            ageTextField.setInvalid(true);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Limpiamos el formulario y
     * ponemos el foco en el campo de nombre.
     */
    private void cleanForm() {// Clean form
        nameTextField.clear();
        lastNameTextField.clear();
        ageTextField.clear();
        nameTextField.focus();
    }

    /**
     * Actualizamos el grid con los datos de la base de datos.
     */
    private void setGridSampleData() {
        basicGrid.setItems(query -> samplePersonService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
    }
}
