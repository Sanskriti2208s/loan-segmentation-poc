# Loan Segmentation POC – JSF + PrimeFaces

A proof-of-concept web application that replicates a **data segmentation UI**
(similar to the reference screenshot) using **JSF 2.3 + PrimeFaces 11** deployed
on **WildFly 18.0.1**.

---

## Technology Stack

| Layer            | Technology                        |
|------------------|-----------------------------------|
| JDK              | 19                                |
| Application Server | WildFly 18.0.1.Final (JBoss)   |
| JSF Runtime      | Mojarra 2.3.x (bundled in WildFly) |
| UI Components    | PrimeFaces 11.0.0                 |
| CSS Layout       | Custom CSS Grid + PrimeFaces saga theme |
| CDI              | Weld 3.x (bundled in WildFly)     |
| Build            | Maven 3.8+                        |

---

## Project Structure

```
loan-segmentation-poc/
├── pom.xml
└── src/main/
    ├── java/com/poc/segmentation/
    │   ├── bean/
    │   │   └── SegmentationBean.java     ← CDI @ViewScoped backing bean
    │   └── model/
    │       ├── LoanData.java             ← Report table row model (3 row types)
    │       ├── Segment.java              ← A named set of criteria fields
    │       ├── SegmentField.java         ← Single selectable field
    │       └── SegmentGroup.java         ← Container for multiple segments
    ├── resources/META-INF/
    │   └── beans.xml                     ← CDI bean-discovery-mode=all
    └── webapp/
        ├── WEB-INF/
        │   ├── web.xml                   ← FacesServlet, theme, project stage
        │   ├── faces-config.xml          ← JSF 2.3 config (minimal)
        │   └── jboss-web.xml             ← WildFly context root
        ├── resources/css/
        │   └── styles.css                ← Custom overrides
        └── index.xhtml                   ← Single-page segmentation UI
```

---

## Build

```bash
# Requires Maven 3.8+ and JDK 19 on PATH
cd loan-segmentation-poc
mvn clean package
```

The build produces `target/loan-segmentation-poc.war`.

---

## Deploy to WildFly 18.0.1

### Option A – Copy to deployments folder

```bash
cp target/loan-segmentation-poc.war  $WILDFLY_HOME/standalone/deployments/
```

WildFly hot-deploys the WAR automatically.

### Option B – WildFly CLI

```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect \
  --command="deploy target/loan-segmentation-poc.war --force"
```

### Option C – Admin Console

1. Open `http://localhost:9990`
2. Go to **Deployments → Add → Upload Deployment**
3. Select the WAR file and click **Finish**

---

## Access

```
http://localhost:8080/loan-segmentation-poc/
```

---

## UI Features

| Feature | Implementation |
|---|---|
| Top navigation bar | Plain HTML `div` with PrimeFaces `p:commandButton` and `p:selectOneMenu` |
| Fields / Segments toggle | Two `p:commandButton` styled as pills; `showFields` flag in bean |
| Live field search | `p:inputText` + `p:ajax event="keyup"` with 300 ms delay |
| Field list | `ui:repeat` over `filteredFields`; used fields highlighted in blue |
| Segment group selector | `p:selectOneMenu` bound to `selectedGroupName` |
| Add / Remove segment field | `p:commandLink` / `p:commandButton` with EL method params |
| Active segment indicator | CSS `.segment-active` border when `segment == activeSegment` |
| Report preview table | `p:dataTable` with three CSS row-type classes for hierarchy |
| Include Empty Segments | `p:toggleSwitch` wired to `includeEmptySegments` |
| Auto Refresh toggle | `p:toggleSwitch` wired to `autoRefresh` |
| Feedback toasts | `p:growl` driven by `FacesContext.addMessage()` |
| Responsive layout | CSS Grid collapses from 3-col → 2-col → 1-col |
