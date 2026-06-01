---
name: software-engineering-basics
description: "Create course-style requirements, UML, analysis, design, database, class, and sequence artifacts from local references."
---

# Software Engineering Basics

## Workflow

1. Identify the requested artifact: requirements, analysis, or design.
2. Read only the matching reference file from `references/`; use `references/table_of_contents.md` first when the task spans multiple artifacts.
3. Mirror the reference style, terminology, and level of detail before adding new content.
4. Keep outputs concrete and course-ready: actors, use cases, scenarios, entities, classes, relationships, messages, tables, keys, and constraints should be explicit.
5. Prefer Vietnamese for final deliverables unless the user asks for English.

## Reference Map

- Requirements in natural language: `references/requirements_natural_lang.md`
- Requirements in UML/use-case style: `references/requirements_uml.md`
- Standard and exceptional scenarios: `references/analysis_scenario.md`
- Entity extraction during analysis: `references/analysis_entity_class_extraction.md`
- Analysis class diagrams: `references/analysis_class_diagram.md`
- Analysis sequence diagrams: `references/analysis_sequence_diagram.md`
- Designed entity classes: `references/design_entity_class.md`
- Database design: `references/design_database.md`
- Design class diagrams: `references/design_class_diagram.md`
- Design sequence diagrams: `references/design_sequence_diagram.md`

## Output Rules

- Preserve the distinction between analysis and design. Analysis describes business concepts and responsibilities; design adds implementation-oriented classes, persistence, boundaries, controls, and technical messages.
- For requirements, make actors, goals, preconditions, main flows, alternative flows, and business rules traceable.
- For class work, separate entity, boundary, and control responsibilities when the reference does so.
- For sequence diagrams, make message order and participating objects unambiguous; include exceptional flows when requested.
- For database design, include tables, primary keys, foreign keys, important attributes, and relationship cardinalities.
- If the target project is not the hotel reservation case study, adapt structure and reasoning from the references instead of copying domain-specific hotel content.

## Verification

Before finalizing, check that the requested artifact is complete, internally consistent, and traceable to the previous lifecycle step. If editing files, verify the written file directly and scan for placeholder text or conflict markers.
