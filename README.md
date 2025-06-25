# HexOust

A turn-based, two-player hexagonal strategy game built in Java & JavaFX as part of UCD’s COMP20050 Software Engineering module.

## Table of Contents

1. [Overview](#overview)  
2. [Features](#features)  
3. [Architecture & Design](#architecture--design)  
4. [Testing](#testing)  
5. [Documentation](#documentation)  
6. [Contributing](#contributing)  
7. [License](#license)

---

## Overview

HexOust is a base-7 (13×13) hex-grid strategy game in which two players alternate placing stones to surround and capture enemy groups. Captures remove the opponent’s stones and grant an extra turn. The first player to eliminate all opponent stones (after the opponent has placed at least one) wins.

---

## Features

- **Custom Hex Grid**  
  – 13×13 board with pixel⇄hex coordinate conversion for precise click handling.  
- **Rule Enforcement**  
  – Adjacency, occupancy, and first-move logic enforced by `MoveValidator`.  
- **Capture Mechanics**  
  – DFS-driven `CaptureHandler` finds and removes smaller opponent groups.  
- **Extra Turn Logic**  
  – Captures award the capturing player an immediate extra move.  
- **Interactive UI**  
  – JavaFX `Canvas` rendering, turn indicators, restart/exit buttons, and modal end-game dialog.  
- **Robust Testing**  
  – >90% coverage with JUnit 5 unit tests and JavaFX integration tests.  

---

## Architecture & Design

![Class Diagram](docs/uml/class-diagram.png)  
*High-level interplay of core classes*

- **Board**  
  – Manages grid state, stone placement, and rendering.  
  – Provides `pixelToHex()`/`hexToPixel()` conversions.  
- **Player**  
  – Tracks current player color, captures, and extra-turn flag.  
- **MoveValidator**  
  – Validates placement rules: empty cell, adjacency to own stones (except first move).  
- **CaptureHandler**  
  – Uses Depth-First Search to detect and remove surrounded enemy groups; triggers extra turns.  
- **GameManager**  
  – Core loop coordinating input → validation → capture → win check → UI update.  
- **InputHandler & Renderer**  
  – JavaFX controllers for buttons, canvas events, and on-screen messages.



## Testing

Automated tests ensure correctness and prevent regressions.

# Run all tests
mvn test

* **Unit Tests**

  * `MoveValidatorTest.java`
  * `BoardTest.java`
  * `CaptureHandlerTest.java`
* **Integration Tests**

  * `GameManagerTest.java`
  * `PlayerTest.java`
  * JavaFX setup via `JavaFXTestInitializer.java`

## Documentation

* **Sprint Plan & Requirements**: `docs/Sprint Plan.pdf`
* **Design Diagrams**: `docs/uml/`
* **Module Report & Rubric**:

  * `docs/Group Report SEP 2 grp-35.pdf`
  * `docs/COMP20050_FinalProjectSubmission_Rubric.pdf`

## Contributing

1. Fork the repository
2. Create a feature branch

   ```bash
   git checkout -b feature/YourFeature
   ```
3. Commit your changes with clear messages
4. Push to your branch

   ```bash
   git push origin feature/YourFeature
   ```
5. Open a Pull Request for review

Please follow the existing code style and include tests for any new functionality.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.


