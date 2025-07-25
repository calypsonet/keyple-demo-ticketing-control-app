# Keyple Control Demo

This is the repository for the Keyple Android Control Demo application.

This demo is an open source project provided by the [Calypso Networks Association](https://calypsonet.org) implementing
the [Eclipse Keyple SDK](https://keyple.org) in a typical use case that can serve as a basis for building a ticketing
ecosystem based on contactless cards and/or NFC smartphones.

The source code and APK are available at  [calypsonet/keyple-demo-ticketing-control-app/releases](https://github.com/calypsonet/keyple-demo-ticketing-control-app/releases)

The code can be easily adapted to other cards, terminals and business logic.

It shows how to easily control a card following a validation performed with the [Keyple Demo Validation](https://github.com/calypsonet/keyple-demo-ticketing-validation-app)
application (entry into the transportation network with a Season Pass and/or Multi-trip ticket).
The contracts being updated in the Calypso card with the Android application of the [Keyple Reload Demo package](https://github.com/calypsonet/keyple-demo-ticketing-reloading-remote).

The demo application was tested on the following terminals:
- `Famoco FX205` via the open source plugins [Famoco](https://github.com/calypsonet/keyple-famoco) (for SAM access) and [Android NFC](https://keyple.org/components-java/plugins/nfc/) (for card access).
- `Coppernic C-One 2` via the open source plugin [Coppernic](https://github.com/calypsonet/keyple-android-plugin-coppernic).
- `Standard NFC smartphone` via the open source plugin [Android NFC](https://keyple.org/components-java/plugins/nfc/).

The following terminals have also been tested but as they require non-open source libraries, they are not active by default (see [Using proprietary plugins](#using-proprietary-plugins))
- `Bluebird EF501` via the proprietary plugin [Bluebird](https://github.com/calypsonet/keyple-plugin-cna-bluebird-specific-nfc-java-lib).
- `Flowbird Axio 2` via the proprietary plugin [Flowbird](https://github.com/calypsonet/keyple-android-plugin-flowbird).

All exchanges made with the controlled card are potentially cryptographically certified by a security module (SAM) when
it is installed and available.
However, if this is not the case, the validity of the card is made from a simple reading of the data (this is always
the case when the application is launched on a standard smartphone).

## Supported Card Types

This application supports two types of cards:

### Calypso Cards
Standard Calypso cards that support secure sessions with SAM authentication. These cards can contain multiple contracts (up to 4 depending on the product type: Basic=1, Light=2, Prime/Regular=4) and use secure cryptographic operations.

### Storage Cards
Simple storage cards that don't require SAM authentication. These cards contain a single contract and use basic read/write operations without cryptographic security.

**Important Note**: The Storage Card implementation in this demo is intentionally basic and does not account for potential specific capabilities that different types of storage cards might offer, such as dedicated counters for specific components, advanced memory management, or card-specific security features. A production application would likely need to consider and implement these card-specific capabilities based on the actual storage card technology being used.

**Security Consideration**: No security mechanisms have been implemented for Storage Cards in this demonstration. In a production environment, developers should implement appropriate security measures to protect data exchanges, for example by using signatures generated by a SAM or other cryptographic security modules suitable for their specific use case and security requirements.

## Keyple Demos

This demo is part of a set of three demos:
* [Keyple Reload Demo](https://github.com/calypsonet/keyple-demo-ticketing-reloading-remote)
* [Keyple Validation Demo](https://github.com/calypsonet/keyple-demo-ticketing-validation-app)
* [Keyple Control Demo](https://github.com/calypsonet/keyple-demo-ticketing-control-app)

These demos are all based on a common library that defines elements such as constants and data structures implemented
for the logic of the ticketing application: [Keyple Demo Common Library](https://github.com/calypsonet/keyple-demo-ticketing-common-lib).

Please refer to the [README](https://github.com/calypsonet/keyple-demo-ticketing-common-lib/blob/main/README.md)
file of this library to discover these data structures. All enumerated types used across the three demo applications (Priority Codes, Version Numbers, etc.) are defined in this common library to ensure consistency and interoperability between the different components of the ticketing ecosystem.

## Control Procedure

### Control Use Case

The control use case will first analyze the event to obtain the information from the contract that was used in the last
validation and then read and collect the information of all contracts present in the card and their validity status.

The list of contract returned must clearly mark the contract that was used in the validation (if any).

Steps:
1. Detection and Selection
2. Event Analysis
3. Contract Analysis

### Process for Calypso Cards

For this control demo application, a simple example control procedure has been implemented.
This procedure is implemented in the `CalypsoCardRepository` class.

Opening a Calypso secure session is optional for this procedure since we do not need to write anything on the card.
So if the Calypso SAM is present at the beginning we set the `isSecureSessionMode` to true, but we keep on with the procedure if not.

This procedure's main steps are as follows:
- **Detection and Selection**
  - Detection Analysis:
    - If AID not found reject the card.
  - Selection Analysis:
    - If File Structure unknown reject the card.
- **Environment Analysis:**
  - Read the environment record:
    - Open a secure session if `isSecureSessionMode` is true.
    - Read the environment record.
  - Unpack environment structure from the binary present in the environment record:
    - If `EnvVersionNumber` of the `Environment` structure is not the expected one (==`VersionNumber.CURRENT_VERSION` for the current version) reject the card. Abort transaction if `isSecureSessionMode` is true.
    - If `EnvEndDate` points to a date in the past reject the card. Abort transaction if `isSecureSessionMode` is true.
- **Event Analysis:**
  - Read and unpack the last event record:
    - If `EventVersionNumber` is not the expected one (==`VersionNumber.CURRENT_VERSION` for the current version) reject the card (if ==`VersionNumber.UNDEFINED` return error status indicating clean card). Abort Transaction if `isSecureSessionMode` is true.
    - If `EventLocation` != value configured in the control terminal set the validated contract **not valid** flag as true and go to **Contract Analysis**.
    - Else If `EventDateStamp` points to a date in the past set the validated contract **not valid** flag as true and go to **Contract Analysis**.
    - Else If (`EventTimeStamp` + Validation period configured in the control terminal) < current time of the control terminal set the validated contract **not valid** flag as true.
- **Contract Analysis**: For each contract:
  - Read all contracts and the counter file.
  - For each contract:
  - Unpack the contract
  - If the `ContractVersionNumber` == `VersionNumber.UNDEFINED` then the contract is blank, move on to the next contract.
  - If `ContractVersionNumber` is not the expected one (==`VersionNumber.CURRENT_VERSION` for the current version) reject the card. Abort Transaction if `isSecureSessionMode` is true.
  - If `ContractAuthenticator` is not 0 perform the verification of the value by using the PSO Verify Signature command of the SAM (TODO: implementation pending).
  - If `ContractValidityEndDate` points to a date in the past mark contract as Expired.
  - If `EventContractUsed` points to the current contract index & **not valid** flag is false then mark it as Validated.
  - If the `ContractTariff` value for the contract is `PriorityCode.MULTI_TRIP`, unpack the counter associated to the contract to extract the counter value.
  - Add contract data to the list of contracts read to return to the upper layer.
  - If `isSecureSessionMode` is true, close the Validation session.
  - Return the status of the operation to the upper layer. <Exit process>

### Process for Storage Cards

For Storage Cards, a simplified control procedure is implemented in the `StorageCardRepository` class.

Storage Cards do not require SAM authentication and use simple read/write operations. The procedure is similar but adapted for the simpler card structure:

**Important Limitations**: This implementation is a basic demonstration and does not leverage specific capabilities that various storage card types might offer. Production applications should be adapted to take advantage of card-specific features such as:
- Dedicated counter management for different card components
- Advanced memory organization and access patterns
- Card-specific security features or encryption capabilities
- Optimized read/write operations based on card technology

**Security Notice**: This demonstration does not implement any security mechanisms for Storage Cards. Production applications should implement appropriate security measures such as:
- Data encryption and authentication
- Signature verification using SAM or other security modules
- Secure key management and exchange protocols
- Protection against replay attacks and data tampering

The control procedure steps are:

- **Detection and Selection:**
  - Same as Calypso cards but without secure session requirements.
- **Environment and Event Analysis:**
  - Read environment, event, and contract data in a single operation using block reads.
  - Perform the same version and date validations as Calypso cards.
  - Evaluate event validity based on location, date, and time criteria.
- **Contract Analysis:**
  - Storage Cards have only one contract, so analysis is simplified.
  - Validate the contract version and validity dates.
  - Check contract authentication if required (though no security is implemented in this demo).
  - Determine if the contract was used in the last validation event.
  - Extract counter values for `PriorityCode.MULTI_TRIP` and `PriorityCode.STORED_VALUE` contracts.
  - Mark the contract status (validated, expired, etc.) based on the analysis.
- **Result Generation:**
  - Create the contract information for display.
  - Include validation event details if available.
  - Close the transaction without secure session procedures.

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

Below are the description of the classes useful for implementing the ticketing layer:
- `TicketingService`
- `ReaderRepository`
- `ReaderActivity.CardReaderObserver`
- `CalypsoCardRepository` / `StorageCardRepository`

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

Once a card is detected, the service processes the selection scenario by retrieving the current `CalypsoCard` or `StorageCard` object.
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

Two implementations are provided:
- `CalypsoCardRepository`: For Calypso cards with secure session support
- `StorageCardRepository`: For simple storage cards without secure session requirements

## Priority Codes

The application uses the following priority codes for contract management:

- `PriorityCode.SEASON_PASS`: Season pass contract (highest priority)
- `PriorityCode.MULTI_TRIP`: Multi-trip ticket contract
- `PriorityCode.STORED_VALUE`: Stored value contract
- `PriorityCode.FORBIDDEN`: Contract is forbidden for use
- `PriorityCode.EXPIRED`: Contract has expired
- `PriorityCode.UNKNOWN`: Unknown contract type
- `PriorityCode.UNDEFINED`: Undefined/uninitialized contract

The control procedure analyzes contracts and displays their status, including whether they were used in the last validation event.

## Using proprietary plugins

By default, proprietary plugins are deactivated.
If you want to activate them, then here is the procedure to follow:
1. make an explicit request to [CNA](https://calypsonet.org/contact-us/) to obtain the desired plugin,
2. copy the plugin into the `/app/libs/` directory,
3. delete in the `/app/libs/` directory the plugin with the same name but suffixed with `-mock` (e.g. xxx-mock.aar),
4. compile the project via the gradle `build` command,
5. deploy the new apk on the device.