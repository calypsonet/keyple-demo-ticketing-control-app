# Keyple Control Demo

This is the repository for the Keyple Android Control Demo application. 

This demo is an open source project provided by [Calypso Networks Association](https://calypsonet.org),
you can adapt the demo for your cards, terminals, projects, etc. 

This demo shows how to easily control a contract (Season Pass and/or Multi-trip ticket) written on a Calypso card
using the [Eclipse Keyple](https://keyple.org) components.

The demo application runs on the following devices:
- `Bluebird` via the proprietary plugin [Bluebird](https://github.com/calypsonet/keyple-plugin-cna-bluebird-specific-nfc-java-lib).
- `Coppernic` via the open source plugin [Coppernic](https://github.com/calypsonet/keyple-android-plugin-coppernic).
- `Famoco` via the open source plugins [Famoco](https://github.com/calypsonet/keyple-famoco) (for SAM access) and [Android NFC](https://keyple.org/components-java/plugins/nfc/) (for card access).
- `Flowbird` via the proprietary plugin [Flowbird](https://github.com/calypsonet/keyple-android-plugin-flowbird).

The source code and APK are available at  [calypsonet/keyple-android-demo-control/releases](https://github.com/calypsonet/keyple-android-demo-control/releases)

By default, proprietary plugins are deactivated.
If you want to activate them, then here is the procedure to follow:
1. make an explicit request to CNA to obtain the desired plugin,
2. copy the plugin into the `/app/libs/` directory,
3. delete in the `/app/libs/` directory the plugin with the same name but suffixed with `-mock` (e.g. xxx-mock.aar),
4. compile the project via the gradle `build` command,
5. deploy the new apk on the device.

## Keyple Demos

This demo is part of a set of three demos:
* [Keyple Remote Demo](https://github.com/calypsonet/keyple-java-demo-remote)
* [Keyple Validation Demo](https://github.com/calypsonet/keyple-android-demo-validation)
* [Keyple Control Demo](https://github.com/calypsonet/keyple-android-demo-control)

## Calypso Card Applications

The demo works with the cards provided in the [Test kit](https://calypsonet.org/technical-support-documentation/)

This demo can be used with Calypso cards with the following configurations:
* AID 315449432E49434131h - File Structure 05h (CD Light/GTML Compatibility)
* AID 315449432E49434131h - File Structure 02h (Revision 2 Minimum with MF files)
* AID 315449432E49434133h - File Structure 32h (Calypso Light Classic)
* AID A0000004040125090101h - File Structure 05h (CD Light/GTML Compatibility)

## Data Structures

The data structures are defined in the library common to all the demo applications [Keyple Demo Common Library](https://github.com/calypsonet/keyple-demo-common-lib).

## Control Procedure

### Control Use Case

The control use case will first analyze the event to obtain the information from the contract that was used in the last
validation and then read and collect the information of all contracts present in the card and their validity status.

The list of contract returned must clearly mark the contract that was used in the validation (if any). 

Steps:
1. Detection and Selection
2. Event Analysis
3. Contract Analysis

### Process

For this control demo application, a simple example control procedure has been implemented. 
This procedure is implemented in the `CardRepository` class.

Opening a Calypso secure session is optional for this procedure since we do not need to write anything on the card.
So if the Calypso SAM is present at the beginning we set the `isSecureSessionMode` to true, but we keep on with the procedure if not.

This procedure's main steps are as follows:
- Detection and Selection
  - Detection Analysis:
    - If AID not found reject the card.
  - Selection Analysis:
    - If File Structure unknown reject the card.
- Environment Analysis:
  - Read the environment record:
    - Open a secure session if `isSecureSessionMode` is true.
    - Read the environment record.
  - Unpack environment structure from the binary present in the environment record:
    - If `EnvVersionNumber` of the `Environment` structure is not the expected one (==1 for the current version) reject the card. Abort transaction if `isSecureSessionMode` is true.
    - If `EnvEndDate` points to a date in the past reject the card. Abort transaction if `isSecureSessionMode` is true.
- Event Analysis:
  - Read and unpack the last event record:
    - If `EventVersionNumber` is not the expected one (==1 for the current version) reject the card (if ==0 return error status indicating clean card). Abort Transaction if `isSecureSessionMode` is true.
    - If `EventLocation` != value configured in the control terminal set the validated contract **not valid** flag as true and go to **Contract Analysis**.
    - Else If `EventDateStamp` points to a date in the past set the validated contract **not valid** flag as true and go to **Contract Analysis**.
    - Else If (`EventTimeStamp` + Validation period configure in the control terminal) < current time of the control terminal set the validated contract **not valid** flag as true.
- **Contract Analysis**: For each contract:
  - Read all contracts and the counter file.
  - For each contract:
  - Unpack the contract
  - If the `ContractVersionNumber` == 0 then the contract is blank, move on to the next contract.
  - If `ContractVersionNumber` is not the expected one (==1 for the current version) reject the card. Abort Transaction if `isSecureSessionMode` is true.
  - If `ContractValidityEndDate` points to a date in the past mark contract as Expired.
  - If `EventContractUsed` points to the current contract index & **not valid** flag is false then mark it as Validated.
  - If the `ContractTariff` value for the contract is 2 or 3, unpack the counter associated to the contract to extract the counter value.
  - Add contract data to the list of contracts read to return to the upper layer.
  - If `isSecureSessionMode` is true, close the Validation session.
  - Return the status of the operation to the upper layer. <Exit process>

## Screens

- Device selection (`DeviceSelectionActivity`): Allows you to indicate the type of device used, in order to use the associated plugin.
  - Initially, devices using proprietary plugins are grayed out.
- Settings (`SettingsActivity`): Allows to set the settings of the control procedure:
  - Location: Where the control is taking place. If the validation occurred in a different location, the controlled contract will not be considered as valid.
  - Validity Duration: Period (in minutes) during which the contract is considered valid.
- Home (`HomeActivity`): Allows to launch the card detection phase.
- Reader (`ReaderActivity`): Initializes the Keyple plugin. At this point the user must present the card that he wishes to control.
  - Initialize the Keyple plugin: start detection on NFC and SAM (if available) readers.
  - Prepare and defines the default selection requests to be processed when a card is inserted.
  - Listens to detected cards.
  - Launches the Control Procedure when a card is detected.
- Control result screen (`CardContentActivity`): displays the controlled card content.
  - Contracts list.
  - Last validation event.
- Invalid control screen (`NetworkInvalidActivity`): displayed when the control procedure failed.

## Ticketing implementation

Below are the classes useful for implementing the ticketing layer:
- `TicketingService`
- `ReaderRepository`
- `ReaderActivity.CardReaderObserver`
- `CardRepository`

### TicketingService

This service is the orchestrator of the ticketing process.

Mainly used to manage the lifecycle of the Keyple plugin.
This service is used to initialize the plugin and manage the card detection phase.
It is called on the different steps of the reader activity lifecycle:
- onResume:
  - Initialize the plugin (Card and SAM readers...)
  - Get the ticketing session
  - Start NFC detection
- onPause:
  - Stop NFC detection
- onDestroy:
  - Clear the Keyple plugin (remove observers and unregister plugin)
  
It prepares and scheduled the selection scenario that will be sent to the card when a card is detected by setting
the AID(s) and the reader protocol(s) of the cards we want to detect and read.

Once a card is detected, the service processes the selection scenario by retrieving the current `CalypsoCard` object.
This object contains information about the card (serial number, card revision...)

Finally, this class is responsible for launching the control procedure and returning its result.

### ReaderRepository

This service is the interface between the business layer and the reader.

### ReaderActivity.CardReaderObserver

This class is the reader observer and inherits from Keyple class `CardReaderObserverSpi`

It is invoked each time a new `CardReaderEvent` (`CARD_INSERTED`, `CARD_MATCHED`...) is launched by the Keyple plugin.
This reader is registered when the reader is registered and removed when the reader is unregistered.

### CardRepository

This class contains the implementation of the "Control" procedure.