# Game App Testing: A JavaFX Othello Case Study

This project explores **game app testing** through a **JavaFX-based Othello game** developed in Java.  
The project focuses on testing both **core game logic** and **GUI behavior** using automated testing tools, and also explores how **AI can support test review and improvement**.

## Project Overview

The Othello game was originally created as a previous Java course project and was reused here as a practical case study for testing.

The testing work focuses on key game behaviors such as:
- move validation
- disc flipping
- turn switching
- game-over and winner detection

## Tools Used

- **JUnit 4** — for logic-level unit testing
- **TestFX** — for JavaFX GUI testing
- **AssertJ** — for readable assertions
- **JaCoCo** — for code coverage analysis

## Test Scope

The test suite covers:
- core Othello game logic
- board state changes
- GUI interaction paths
- selected state/command behaviors
- AI-assisted test improvement exploration

## AI Exploration

AI was used as a **test review and planning assistant**.  
It was asked to:
- review the current test suite
- identify missing edge cases
- suggest additional test scenarios
- support test improvement through iterative prompting

This project found that AI was useful for generating ideas and identifying gaps, but it still required human review and refinement.

## Coverage Summary

Before AI-assisted test additions:
- Line coverage: 44.5%
- Branch coverage: 52.1%

After AI-assisted test additions:
- Line coverage: 48.5%
- Branch coverage: 52.8%

The largest improvements were seen in GUI- and command-related logic, while board-level rule coverage still has room for improvement.
