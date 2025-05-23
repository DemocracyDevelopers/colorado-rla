ColoradoRLA
===========

[![Build Status](https://travis-ci.org/FreeAndFair/ColoradoRLA.svg?branch=master)](https://travis-ci.org/FreeAndFair/ColoradoRLA)

The **ColoradoRLA** system is software to facilitate risk-limiting
audits at the state level, developed for Colorado's Department of
State in July and August of 2017.

* Blog announcement: [Free & Fair to build risk-limiting audit system for State of Colorado](http://freeandfair.us/blog/risk-limiting-audits/)

* *To be written:* Project Background

* *To be written:* Future Work

Installation and Use
====================

A document describing how to download, install, and use this system is
found in [the docs directory](docs/15_installation.md).

System Documentation
====================

Documentation about this project and the Colorado RLA system includes:
* documentation related to IRV extensions, available at [the Democracy Developers github repository](https://github.com/DemocracyDevelopers/Colorado-irv-rla-educational-materials),
  - a slide deck summarizing the main ideas,
  - the Guide To RAIRE, Parts 1 and 2, describing the background theory for IRV auditing,
  - the Implementation Report, detailing the implementation describing the user workflow
    changes from plurality-only audits,
* instructions for running the IRV-extended system, including both colorado-rla and the [raire-service](https://github.com/DemocracyDevelopers/raire-service),
  in the [raire-service README](https://github.com/DemocracyDevelopers/raire-service/blob/main/README.md).
 
Some older documentation may be somewhat out of date, but useful for non-IRV aspects of colorado-rla:
* a [User Manual (docx)](docs/user_manual.docx)
  with an overview of the system,
* a [County Run Book (docx)](docs/county_runbook.docx) and
  [State Run Book (docx)](docs/sos_runbook.docx) for system users,
* a [description of our development process and methodology](docs/35_methodology.md),
* a [developer document](docs/25_developer.md) that contains our
  developer instructions, including the project history, technologies
  in use, dependencies, how to build the system, how we perform
  quality assurance, how we perform validation and verification, and
  what the build status of the project is,
* the [system requirements](docs/50_requirements.md),
* the [formal system specification](docs/55_specification.md),
* the [means by which we validate and verify the system](docs/40_v_and_v.md),
* a [glossary](docs/89_glossary.md) of the domain terminology used in
  the system,
* a full [bibliography](docs/99_bibliography.md) is available.
* a [document describing how we perform project management](docs/30_project_management.md),
* the [license](LICENSE.md) under which this software is made available,
  and
* all [contributors](#contributors) to the design and development of
  this system are listed below,

  

Contributors
============

* Joey Dodds (Principled Computer Scientist) RLA core computations
  implementation
* Joseph Kiniry (Principled CEO and Chief Scientist) Project Head,
  author of formal specification, design and implementation of ASMs
  and 2FA
* Neal McBurnett (Principled Elections Auditing Expert) RLA expert,
  design and implementation of data export application
  and automatic server test infrastructure
* Morgan Miller (Principled Usability Specialist) UX expert, conducted
  interviews with CDOS and County personnel, initial UI design
* Joe Ranweiler (Principled Computer Scientist) Principal author of
  RLA Tool Client
* Daniel Zimmerman (Principled Computer Scientist) Principal author of
  RLA Tool Server
* Mike Prasad (CDOS Developer/Architect) Authored enhancements to RLA Tool Client and Server
* Rich Helton (CDOS Developer) Authored enhancements to RLA Tool Client and Server
* Dogan Cibiceli (CDOS Developer) Authored enhancements to RLA Tool Client and Server
* [Democracy Developers](https://www.democracydevelopers.org.au/) 
  - Michelle Blom
  - Andrew Conway
  - Vanessa Teague
implemented IRV extensions based on original research by Michelle Blom, Peter Stuckey and Vanessa Teague.

More information about our team members [is available](docs/70_team.md).
