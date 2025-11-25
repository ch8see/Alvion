# Collaboration & build guide for the `doc` folder

This document explains how to collaborate on `doc/designDoc.tex` and the `doc/assets` images, how to build the PDF locally, clean LaTeX auxiliary files before committing, and how to push a branch and open a PR with a clear description.

## Quick overview

- Work on a feature branch (don't edit `main` directly).
- Make changes to `designDoc.tex` or files in `doc/assets/`.
- Build locally to verify (use `latexmk`).
- Clean auxiliary files before committing.
- Commit only the intended source changes (avoid committing aux files). If you must include generated PDF, see notes below.
- Push your branch and open a PR with a summary and checklist.

## Prerequisites

- macOS (instructions here assume zsh)
- A TeX distribution (TeX Live, MacTeX, etc.) with `latexmk` available
- git configured with access to the repo
- Optional tools: ImageMagick (`magick`) or Ghostscript (`gs`) for converting large PDFs to PNGs

## Recommended branch workflow

1. Update `main` and create a feature branch:

```bash
# fetch latest from origin
git fetch origin

# switch to main and update
git switch main
git pull origin main

# create a feature branch from main
git switch -c feature/short-description
```

2. Make your changes (edit `doc/designDoc.tex`, add or update images in `doc/assets/`).

3. Build locally to verify the document renders and images fit correctly (see Build step).

4. Clean auxiliary files (see Clean step) and commit only the source and asset files you changed.

5. Push and open a PR:

```bash
# stage your changes
git add path/to/changed/files

# commit with a concise, conventional message
git commit -m "docs: Short summary of the change"

# push branch and set upstream
git push --set-upstream origin feature/short-description
```

Then open a PR in your Git hosting provider (GitHub/GitLab/Bitbucket). Use the PR template below.

## Build (how to compile the LaTeX document)

Preferred (uses `latexmk` which handles repeated runs and bib/aux files):

```bash
cd doc
latexmk -pdf -file-line-error -interaction=nonstopmode designDoc.tex
```

Alternative (manual):

```bash
cd doc
pdflatex -file-line-error -interaction=nonstopmode designDoc.tex
# run twice to fix references / TOC if needed
pdflatex -file-line-error -interaction=nonstopmode designDoc.tex
```

Notes:

- If pdf build fails with Unicode errors, either replace the offending Unicode in the .tex file (use math-mode for symbols like τ: `$\\tau$`) or build with XeLaTeX/LuaLaTeX if the document is Unicode-heavy.
- Avoid filenames with spaces or parentheses in `doc/assets/` — they can cause hyperref/bookmark errors. Use hyphens or underscores instead.

## Clean (remove auxiliary files before committing)

It's recommended to remove LaTeX auxiliary files before committing. `latexmk` has a clean helper:

```bash
cd doc
latexmk -c
```

This removes common aux files (like `.aux`, `.log`, `.fdb_latexmk`, `.fls`, etc.).

If you need to remove them manually:

```bash
cd doc
rm -f *.aux *.log *.out *.toc *.fls *.fdb_latexmk *.lof *.lot
```

Confirm with `git status` that only intended (source/asset) files are staged.

## If you want to include the generated PDF in the repo

Recommendation: generally avoid committing generated files (PDFs) to the repository. Keep repo focused on source (`.tex`) and assets. If your team policy requires committing the built PDF, do this:

1. Build the PDF (see Build step).
2. Stage the PDF explicitly:

```bash
cd doc
git add designDoc.pdf
git commit -m "chore(docs): add built designDoc.pdf for review"
git push
```

Then run `latexmk -c` to remove aux files and continue.

## PR title & description template

Use a clear PR title like:

```
docs: clarify architecture diagram and fix image paths
```

Example PR body:

```
What:
- Fixed several image paths in `doc/designDoc.tex` to `doc/assets/`.
- Converted the architecture PDF to a PNG and adjusted scaling so it fits the page.

Why:
- Paths were incorrect and caused build failures for other contributors.
- Rasterized the large diagram to avoid layout issues when including a full-page PDF.

How to test locally:
1. Pull the branch.
2. Run `cd doc && latexmk -pdf designDoc.tex`.
3. Verify the generated PDF opens and the first page/cover looks correct.

Checklist:
- [ ] Build succeeds locally
- [ ] No LaTeX auxiliary files committed
- [ ] Images render and fit pages
- [ ] PR description explains visible changes
```

## Image conversion tips (PDF → PNG)

If a large PDF diagram doesn't fit correctly or causes layout problems, convert it to a high-resolution PNG and include it with `\\includegraphics`.

Recommended command examples:

# ImageMagick (if installed):

```bash
# 300 DPI (good for print); change density for higher/lower resolution
magick -density 300 doc/assets/archDiagram2.pdf -quality 90 doc/assets/archDiagram2.png
```

# Ghostscript (often available):

```bash
gs -dSAFER -dBATCH -dNOPAUSE -r300 -sDEVICE=png16m -sOutputFile=doc/assets/archDiagram2.png doc/assets/archDiagram2.pdf
```

After conversion, update `designDoc.tex` to use the PNG via `\\includegraphics[width=\\textwidth]{assets/archDiagram2.png}` (or another width that fits). Rebuild and verify.

## Troubleshooting common issues

- Unicode errors (e.g., `Unicode character τ not set up`): replace literal Unicode with TeX math (e.g., `$\\tau$`) or build with XeLaTeX/LuaLaTeX.
- Bookmarks / `\\BKM@entry` errors: check for filenames with spaces/parentheses in `doc/assets/`. Rename them to remove spaces.
- Duplicate bookmark warnings: ensure labels are unique and avoid including the same PDF with bookmarks multiple times; `pdfpages` has options to suppress copying of bookmarks if needed.

## Quick checklist before opening a PR

- [ ] Branch created from up-to-date `main`
- [ ] Changes implemented and tested locally
- [ ] Ran `latexmk -pdf` and verified PDF
- [ ] Ran `latexmk -c` (or removed aux files) so no build artifacts are committed
- [ ] Committed source/asset changes with a clear message
- [ ] Pushed branch to remote and opened PR with the template above

---

If you'd like, I can also add a small `.github/PULL_REQUEST_TEMPLATE.md` or a short script that automates build → clean → commit steps. Tell me if you want that added.

File location: `doc/README.md`
