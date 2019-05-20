package ru.bellintegrator.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.bellintegrator.model.types.AddressType;
import ru.bellintegrator.model.types.PersonType;
import ru.bellintegrator.service.AddressService;
import ru.bellintegrator.service.ContractorService;
import ru.bellintegrator.service.PersonService;
import ru.bellintegrator.view.AddressView;
import ru.bellintegrator.view.ContractorView;
import ru.bellintegrator.view.PersonView;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContractorServiceImplTest {

    @Autowired
    private ContractorService contractorService;

    @Autowired
    private PersonService personService;

    @Autowired
    private AddressService addressService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public TestName testName = new TestName();

    private ContractorView testContractorView;
    private PersonView testContactPersonView;
    private PersonView testResponsiblePersonView;
    private AddressView testLegalAddressView;
    private AddressView testAdvertisingAddressView;

    @Before
    public void createTestEnvironment() {
        createTestContractor();
        createTestAddress();
        createTestPerson();
    }

    @After
    public void deleteTestEnvironment() {
        deleteTestContractor();
        deleteTestAddress();
        deleteTestPerson();
    }

    @Test
    public void checkSuccessGetById() {
        ContractorView cv = contractorService.getById(this.testContractorView.getId());
        Assert.assertThat(cv.getName().equals(this.testContractorView.getName()), is(true));
        Assert.assertThat(cv.getLegalAddress().equals(this.testContractorView.getLegalAddress()), is(true));
        Assert.assertThat(cv.getAdvertising().equals(this.testContractorView.getAdvertising()), is(true));
        Assert.assertThat(cv.getResponsible().equals(this.testContractorView.getResponsible()), is(true));
        Assert.assertThat(cv.getContacts().equals(this.testContractorView.getContacts()), is(true));
    }

    @Test
    public void checkSuccessUpdate() {
        this.testContractorView.setName(RandomStringUtils.random(8, true, true));
        this.testLegalAddressView.setCity(RandomStringUtils.random(8, true, false));
        this.testAdvertisingAddressView.setCity(RandomStringUtils.random(8, true, false));
        this.testResponsiblePersonView.setLastName(RandomStringUtils.random(8, true, true));
        this.testContactPersonView.setLastName(RandomStringUtils.random(8, true, true));
        this.testContractorView.setLegalAddress(this.testLegalAddressView);
        this.testContractorView.setAdvertising(this.testAdvertisingAddressView);
        this.testContractorView.setResponsible(this.testResponsiblePersonView);
        ArrayList<PersonView> cvList = new ArrayList<>();
        cvList.add(this.testContactPersonView);
        this.testContractorView.setContacts(cvList);
        ContractorView cv = contractorService.update(this.testContractorView, this.testContractorView.getId());
        Assert.assertThat(cv.getName().equals(this.testContractorView.getName()), is(true));
        Assert.assertThat(cv.getLegalAddress().equals(this.testContractorView.getLegalAddress()), is(true));
        Assert.assertThat(cv.getAdvertising().equals(this.testContractorView.getAdvertising()), is(true));
        Assert.assertThat(cv.getResponsible().equals(this.testContractorView.getResponsible()), is(true));
        Assert.assertThat(cv.getContacts().equals(this.testContractorView.getContacts()), is(true));
    }

    @Test
    public void checkNullResultGetByWrongId() {
        Assert.assertThat(contractorService.getById(-1), is(nullValue()));
    }

    @Test
    public void checkExceptionWhenCreateNullPersonView() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Contractor can't be null");
        contractorService.create(null, -1);
    }

    @Test
    public void checkExceptionWhenUpdateNullPersonView() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Contractor can't be null");
        contractorService.update(null, -1);
    }

    @Test
    public void checkExceptionWhenUpdateWithWrongContractorId() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Can not find address by addressType");
        contractorService.update(this.testContractorView, -1);
    }

    private void createTestContractor() {
        ContractorView testContractor = new ContractorView(
                0,
                RandomStringUtils.random(8, true, true),
                RandomStringUtils.random(18, true, true),
                RandomStringUtils.random(10, true, false),
                RandomStringUtils.random(10, false, true),
                RandomStringUtils.random(10, false, true),
                RandomStringUtils.random(15, true, true)
        );
        this.testContractorView = contractorService.create(testContractor, testContractor.getId());
        Assert.assertThat(contractorService.getById(this.testContractorView.getId()).getName().equals(testContractor.getName()), is(true));
    }

    private void createTestPerson() {
        PersonView testContactPerson = new PersonView(
                0,
                RandomStringUtils.random(15, true, false),
                RandomStringUtils.random(15, true, false),
                RandomStringUtils.random(15, true, false),
                RandomStringUtils.random(10, false, true),
                RandomStringUtils.random(10, true, false),
                PersonType.Contact.getValue(), this.testContractorView.getId()
        );
        this.testContactPersonView = personService.create(testContactPerson, testContactPerson.getContractorId());
        Assert.assertThat(personService.getById(this.testContactPersonView.getId()).getLastName().equals(testContactPerson.getLastName()), is(true));
        ArrayList<PersonView> cvList = new ArrayList<>();
        cvList.add(this.testContactPersonView);
        this.testContractorView.setContacts(cvList);

        PersonView testResponsiblePerson = new PersonView(
                0,
                RandomStringUtils.random(15, true, false),
                RandomStringUtils.random(15, true, false),
                RandomStringUtils.random(15, true, false),
                RandomStringUtils.random(10, false, true),
                RandomStringUtils.random(10, true, false),
                PersonType.Responsible.getValue(), this.testContractorView.getId()
        );
        this.testResponsiblePersonView = personService.create(testResponsiblePerson, testResponsiblePerson.getContractorId());
        Assert.assertThat(personService.getById(this.testResponsiblePersonView.getId()).getLastName().equals(testResponsiblePerson.getLastName()), is(true));
        this.testContractorView.setResponsible(this.testResponsiblePersonView);
    }

    private void createTestAddress() {
        AddressView testLegalAddress = new AddressView(
                0,
                RandomStringUtils.random(8, true, false),
                RandomStringUtils.random(8, true, false),
                RandomStringUtils.random(8, true, true),
                Integer.parseInt(RandomStringUtils.random(3, false, true)),
                Integer.parseInt(RandomStringUtils.random(2, false, true)),
                AddressType.Legal.getValue(), this.testContractorView.getId()
        );
        this.testLegalAddressView = addressService.create(testLegalAddress, testLegalAddress.getContractorId());
        Assert.assertThat(addressService.getById(this.testLegalAddressView.getId()).getCity().equals(testLegalAddress.getCity()), is(true));
        this.testContractorView.setLegalAddress(this.testLegalAddressView);

        AddressView testAdvertisingAddress = new AddressView(
                0,
                RandomStringUtils.random(8, true, false),
                RandomStringUtils.random(8, true, false),
                RandomStringUtils.random(8, true, true),
                Integer.parseInt(RandomStringUtils.random(3, false, true)),
                Integer.parseInt(RandomStringUtils.random(2, false, true)),
                AddressType.Advertising.getValue(), this.testContractorView.getId()
        );
        this.testAdvertisingAddressView = addressService.create(testAdvertisingAddress, testAdvertisingAddress.getContractorId());
        Assert.assertThat(addressService.getById(this.testAdvertisingAddressView.getId()).getCity().equals(testAdvertisingAddress.getCity()), is(true));
        this.testContractorView.setAdvertising(this.testAdvertisingAddressView);
    }

    private void deleteTestContractor() {
        contractorService.delete(this.testContractorView.getId(), this.testContractorView.getId());
        Assert.assertThat(contractorService.getById(this.testContractorView.getId()), is(nullValue()));
    }

    private void deleteTestPerson() {
        personService.delete(this.testContactPersonView.getId(), this.testContactPersonView.getContractorId());
        Assert.assertThat(personService.getById(this.testContactPersonView.getId()), is(nullValue()));
        personService.delete(this.testResponsiblePersonView.getId(), this.testResponsiblePersonView.getContractorId());
        Assert.assertThat(personService.getById(this.testResponsiblePersonView.getId()), is(nullValue()));
    }

    private void deleteTestAddress() {
        addressService.delete(this.testLegalAddressView.getId(), this.testLegalAddressView.getContractorId());
        Assert.assertThat(addressService.getById(this.testLegalAddressView.getId()), is(nullValue()));
        addressService.delete(this.testAdvertisingAddressView.getId(), this.testAdvertisingAddressView.getContractorId());
        Assert.assertThat(addressService.getById(this.testAdvertisingAddressView.getId()), is(nullValue()));
    }
}