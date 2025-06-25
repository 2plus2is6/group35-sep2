# HexOust

A turn-based, two-player hexagonal strategy game built in Java & JavaFX as part of UCD’s COMP20050 Software Engineering module.

## Table of Contents

1. [Overview](#overview)  
2. [Features](#features)  
3. [Architecture & Design](#architecture--design)  
4. [Usage](#usage)  
5. [Testing](#testing)  
6. [Documentation](#documentation)  
7. [Contributing](#contributing)  
8. [License](#license)

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
