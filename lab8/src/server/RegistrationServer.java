package server;

import conference.Conferee;
import conference.Registerable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RegistrationServer implements Registerable {
    private List<Conferee> confereeList;
    private String xmlFileName = "conferees.xml"; // Ім'я XML файлу
    private JFrame frame;

    public RegistrationServer() {
        this.confereeList = new ArrayList<>();
        loadConfereeList(xmlFileName);
        createAndShowGUI();
    }

    @Override
    public synchronized int registerConferee(Conferee conferee) throws RemoteException {
        confereeList.add(conferee);
        return confereeList.size();
    }


    public synchronized String getConfereeList() throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------------------------------------\n");
        sb.append("| № | Ім'я | Прізвище | Організація | Email | Назва доповіді |\n");
        sb.append("------------------------------------------------------------------------------------------\n");
        for (int i = 0; i < confereeList.size(); i++) {
            Conferee conferee = confereeList.get(i);
            sb.append(String.format("| %d | %s | %s | %s | %s | %s |\n",
                    i + 1,
                    conferee.getName(),
                    conferee.getSurname(),
                    conferee.getAffiliation(),
                    conferee.getEmail(),
                    conferee.getPaperTitle()));
        }
        sb.append("------------------------------------------------------------------------------------------\n");
        return sb.toString();
    }

    // Допоміжний метод для створення та додавання дочірнього елемента
    private void createAndAppendChildElement(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        parent.appendChild(element);
    }

    // Збереження списку учасників в XML файл
    public void saveConfereeList(String fileName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Conferees");
            doc.appendChild(rootElement);

            for (Conferee conferee : confereeList) {
                Element confereeElement = doc.createElement("Conferee");
                rootElement.appendChild(confereeElement);
                createAndAppendChildElement(doc, confereeElement, "Name", conferee.getName());
                createAndAppendChildElement(doc, confereeElement, "Surname", conferee.getSurname());
                createAndAppendChildElement(doc, confereeElement, "Affiliation", conferee.getAffiliation());
                createAndAppendChildElement(doc, confereeElement, "Email", conferee.getEmail());
                createAndAppendChildElement(doc, confereeElement, "PaperTitle", conferee.getPaperTitle());
            }

            // Запис DOM в XML файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);
            System.out.println("Список учасників збережено в файл " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Завантаження списку учасників з XML файлу
    public void loadConfereeList(String fileName) {
        try {
            File xmlFile = new File(fileName);
            if (xmlFile.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("Conferee");
                for (int temp = 0; temp < nodeList.getLength(); temp++) {
                    Node nNode = nodeList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        Conferee conferee = new Conferee();
                        conferee.setName(eElement.getElementsByTagName("Name").item(0).getTextContent());
                        conferee.setSurname(eElement.getElementsByTagName("Surname").item(0).getTextContent());
                        conferee.setAffiliation(eElement.getElementsByTagName("Affiliation").item(0).getTextContent());
                        conferee.setEmail(eElement.getElementsByTagName("Email").item(0).getTextContent());
                        conferee.setPaperTitle(eElement.getElementsByTagName("PaperTitle").item(0).getTextContent());
                        confereeList.add(conferee);
                    }
                }
                System.out.println("Список учасників завантажено з файлу " + fileName);
            } else {
                System.out.println("Файл " + fileName + " не знайдено. Створення нового файлу.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Створення графічного інтерфейсу
    private void createAndShowGUI() {
        frame = new JFrame("Сервер реєстрації");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());

        // Кнопка "Вихід"
        JButton exitButton = new JButton("Вихід");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Збереження списку учасників в XML файл перед виходом
                saveConfereeList("conferees.xml");
                System.exit(0);
            }
        });
        frame.add(exitButton);

        frame.setVisible(true);

        try {
            // Запуск RMI-сервера
            Registerable stub = (Registerable) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("RegistrationService", stub);
            System.out.println("Сервер реєстрації запущено.");
        } catch (Exception e) {
            System.err.println("Помилка сервера: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RegistrationServer();
    }
}