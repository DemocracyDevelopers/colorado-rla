ColoradoRLA
===========

[![Build Status](https://travis-ci.org/FreeAndFair/ColoradoRLA.svg?branch=master)](https://travis-ci.org/FreeAndFair/ColoradoRLA)

The **ColoradoRLA** system is software to facilitate risk-limiting
audits at the state level, initiall developed by Free and Fair for Colorado's Department of
State in July and August of 2017. Extensions for Instant Runoff Voting (IRV) were developed
by Democracy Developers in 2023-2025.

System Documentation
====================

Older documentation about this project and the Colorado RLA system is in the legacy-docs folder. It may be somewhat out of date, but is useful for non-IRV aspects of colorado-rla:
* a [User Manual (docx)](legacy-docs/user_manual.docx)
  with an overview of the system,
* a [County Run Book (docx)](legacy-docs/county_runbook.docx) and
  [State Run Book (docx)](legacy-docs/sos_runbook.docx) for system users,
* other information related to development, testing and deployment.

Documentation related to IRV extensions is in the irv-docs folder. 
  - a [slide deck] summarizing the main ideas,
  - the Guide To RAIRE, [Part 1] and [Part 2], describing the background theory for IRV auditing,
  - the Implementation Report, detailing the implementation describing the user workflow
    changes from plurality-only audits,
* instructions for running the IRV-extended system, including both colorado-rla and the [raire-service](https://github.com/DemocracyDevelopers/raire-service),
  in the [raire-service README](https://github.com/DemocracyDevelopers/raire-service/blob/main/README.md).

More recent versions may be available at [the Democracy Developers github repository](https://github.com/DemocracyDevelopers/Colorado-irv-rla-educational-materials),

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
* [Democracy Developers](https://www.democracydevelopers.org.au/) implemented IRV extensions based on original research by Michelle Blom, Peter Stuckey and Vanessa Teague. Lead developers:
  - Michelle Blom
  - Andrew Conway
  - Vanessa Teague

More information about our team members [is available](docs/70_team.md).

License
=======

See the [license](LICENSE.md) under which this software is made available,
