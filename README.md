# Pixel-Based Graphics Editor

A desktop raster and pixel-art editor built in Java with Swing. It provides a
full drawing toolset, undo/redo built on the command pattern, image import and
export, light and dark themes, and a keyboard-driven workflow, all in a single
self-contained application.

## Features

Drawing tools
- Brush with five styles (natural pencil, spray, dotted, oil, star) and three sizes
- Pencil for single-pixel work
- Eraser with four sizes
- Shape tool: rectangle, circle, line, triangle, pentagon, hexagon
- Text tool supporting any installed system font, with adjustable size
- Colour picker with swatches and HSV, HSL, RGB and CMYK modes, plus right-click flood fill

Editing
- Undo and redo, implemented with the command pattern
- Selection tool with move, copy, cut, paste, delete and select-all, plus recolouring of a selected region
- Rotate and flip, applied to the whole canvas, a selection, or an inserted image
- Zoom from 10% to 300%
- Image import, and export to PNG, JPEG or BMP

Application
- Autosave on a configurable interval
- Light and dark themes (FlatLaf)
- Icon-only compact interface mode
- In-app help documentation for every tool
- Full keyboard shortcuts
- Safe-exit prompt to save unsaved work

## Built with

Java 17, Swing, FlatLaf, Gradle.

## Getting started

Requires JDK 17.

```bash
./gradlew build
./gradlew run
```

On Windows, use `gradlew.bat` in place of `./gradlew`. The in-app help (Home then
Help, or Ctrl+H) documents every tool.

## Keyboard shortcuts

| Action | Shortcut |
| ------ | -------- |
| Undo / Redo | Ctrl+Z / Ctrl+Y |
| Save / Save As | Ctrl+S / Ctrl+Shift+S |
| New / Open | Ctrl+N / Ctrl+O |
| Help / Exit | Ctrl+H / Ctrl+Q |
| Brush / Pencil / Eraser | B / P / E |
| Colour / Shape / Zoom / Text | C / S / Z / T |
