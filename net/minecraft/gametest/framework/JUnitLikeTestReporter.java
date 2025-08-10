/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JUnitLikeTestReporter
implements TestReporter {
    private final Document document;
    private final Element testSuite;
    private final Stopwatch stopwatch;
    private final File destination;

    public JUnitLikeTestReporter(File $$0) throws ParserConfigurationException {
        this.destination = $$0;
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.testSuite = this.document.createElement("testsuite");
        Element $$1 = this.document.createElement("testsuite");
        $$1.appendChild(this.testSuite);
        this.document.appendChild($$1);
        this.testSuite.setAttribute("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        this.stopwatch = Stopwatch.createStarted();
    }

    private Element createTestCase(GameTestInfo $$0, String $$1) {
        Element $$2 = this.document.createElement("testcase");
        $$2.setAttribute("name", $$1);
        $$2.setAttribute("classname", $$0.getStructure().toString());
        $$2.setAttribute("time", String.valueOf((double)$$0.getRunTime() / 1000.0));
        this.testSuite.appendChild($$2);
        return $$2;
    }

    @Override
    public void onTestFailed(GameTestInfo $$0) {
        String $$1 = $$0.id().toString();
        String $$2 = $$0.getError().getMessage();
        Element $$3 = this.document.createElement($$0.isRequired() ? "failure" : "skipped");
        $$3.setAttribute("message", "(" + $$0.getTestBlockPos().toShortString() + ") " + $$2);
        Element $$4 = this.createTestCase($$0, $$1);
        $$4.appendChild($$3);
    }

    @Override
    public void onTestSuccess(GameTestInfo $$0) {
        String $$1 = $$0.id().toString();
        this.createTestCase($$0, $$1);
    }

    @Override
    public void finish() {
        this.stopwatch.stop();
        this.testSuite.setAttribute("time", String.valueOf((double)this.stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0));
        try {
            this.save(this.destination);
        } catch (TransformerException $$0) {
            throw new Error("Couldn't save test report", $$0);
        }
    }

    public void save(File $$0) throws TransformerException {
        TransformerFactory $$1 = TransformerFactory.newInstance();
        Transformer $$2 = $$1.newTransformer();
        DOMSource $$3 = new DOMSource(this.document);
        StreamResult $$4 = new StreamResult($$0);
        $$2.transform($$3, $$4);
    }
}

