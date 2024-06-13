--
-- PostgreSQL database dump
--
-- This file includes data from three tiny example files: 
-- - Tiny-IRV-Examples/ThreeCandidatesTenVotes.csv, 
-- - Tiny-IRV-Examples/ThreeCandidatesTenVotesPlusPlurality.csv
-- - badExamples/ThreeCandidatesTenVotesPlusInconsistentPlurality.csv
-- This produces three contests, each across all three contests:
-- - a consistent IRV contest called TinyExample1,
-- - a consistent plurality contest called PluralityExample1,
-- - a mixed IRV/plurality contest called PluralityExample2, which should elicit errors.

-- Dumped from database version 16.3 (Ubuntu 16.3-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.3 (Ubuntu 16.3-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

-- CREATE SCHEMA public;


-- ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: administrator; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.administrator (
    id bigint NOT NULL,
    full_name character varying(255) NOT NULL,
    last_login_time timestamp without time zone,
    last_logout_time timestamp without time zone,
    type character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    version bigint,
    county_id bigint
);


ALTER TABLE public.administrator OWNER TO corlaadmin;

--
-- Name: asm_state; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.asm_state (
    id bigint NOT NULL,
    asm_class character varying(255) NOT NULL,
    asm_identity character varying(255),
    state_class character varying(255),
    state_value character varying(255),
    version bigint
);


ALTER TABLE public.asm_state OWNER TO corlaadmin;

--
-- Name: audit_board; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.audit_board (
    dashboard_id bigint NOT NULL,
    members text,
    sign_in_time timestamp without time zone NOT NULL,
    sign_out_time timestamp without time zone,
    index integer NOT NULL
);


ALTER TABLE public.audit_board OWNER TO corlaadmin;

--
-- Name: audit_intermediate_report; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.audit_intermediate_report (
    dashboard_id bigint NOT NULL,
    report character varying(255),
    "timestamp" timestamp without time zone,
    index integer NOT NULL
);


ALTER TABLE public.audit_intermediate_report OWNER TO corlaadmin;

--
-- Name: audit_investigation_report; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.audit_investigation_report (
    dashboard_id bigint NOT NULL,
    name character varying(255),
    report character varying(255),
    "timestamp" timestamp without time zone,
    index integer NOT NULL
);


ALTER TABLE public.audit_investigation_report OWNER TO corlaadmin;

--
-- Name: ballot_manifest_info; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.ballot_manifest_info (
    id bigint NOT NULL,
    batch_id character varying(255) NOT NULL,
    batch_size integer NOT NULL,
    county_id bigint NOT NULL,
    scanner_id integer NOT NULL,
    sequence_end bigint NOT NULL,
    sequence_start bigint NOT NULL,
    storage_location character varying(255) NOT NULL,
    version bigint,
    ultimate_sequence_end bigint,
    ultimate_sequence_start bigint,
    uri character varying(255)
);


ALTER TABLE public.ballot_manifest_info OWNER TO corlaadmin;

--
-- Name: cast_vote_record; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.cast_vote_record (
    id bigint NOT NULL,
    audit_board_index integer,
    comment character varying(255),
    cvr_id bigint,
    ballot_type character varying(255) NOT NULL,
    batch_id character varying(255) NOT NULL,
    county_id bigint NOT NULL,
    cvr_number integer NOT NULL,
    imprinted_id character varying(255) NOT NULL,
    record_id integer NOT NULL,
    record_type character varying(255) NOT NULL,
    scanner_id integer NOT NULL,
    sequence_number integer,
    "timestamp" timestamp without time zone,
    version bigint,
    rand integer,
    revision bigint,
    round_number integer,
    uri character varying(255)
);


ALTER TABLE public.cast_vote_record OWNER TO corlaadmin;

--
-- Name: comparison_audit; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.comparison_audit (
    id bigint NOT NULL,
    contest_cvr_ids text,
    diluted_margin numeric(10,8) NOT NULL,
    audit_reason character varying(255) NOT NULL,
    audit_status character varying(255) NOT NULL,
    audited_sample_count integer NOT NULL,
    disagreement_count integer NOT NULL,
    estimated_recalculate_needed boolean NOT NULL,
    estimated_samples_to_audit integer NOT NULL,
    gamma numeric(10,8) NOT NULL,
    one_vote_over_count integer NOT NULL,
    one_vote_under_count integer NOT NULL,
    optimistic_recalculate_needed boolean NOT NULL,
    optimistic_samples_to_audit integer NOT NULL,
    other_count integer NOT NULL,
    risk_limit numeric(10,8) NOT NULL,
    two_vote_over_count integer NOT NULL,
    two_vote_under_count integer NOT NULL,
    version bigint,
    overstatements numeric(19,2),
    contest_result_id bigint NOT NULL
);


ALTER TABLE public.comparison_audit OWNER TO corlaadmin;

--
-- Name: contest; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.contest (
    id bigint NOT NULL,
    description character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    sequence_number integer NOT NULL,
    version bigint,
    votes_allowed integer NOT NULL,
    winners_allowed integer NOT NULL,
    county_id bigint NOT NULL
);


ALTER TABLE public.contest OWNER TO corlaadmin;

--
-- Name: contest_choice; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.contest_choice (
    contest_id bigint NOT NULL,
    description character varying(255),
    fictitious boolean NOT NULL,
    name character varying(255),
    qualified_write_in boolean NOT NULL,
    index integer NOT NULL
);


ALTER TABLE public.contest_choice OWNER TO corlaadmin;

--
-- Name: contest_comparison_audit_disagreement; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.contest_comparison_audit_disagreement (
    contest_comparison_audit_id bigint NOT NULL,
    cvr_audit_info_id bigint NOT NULL
);


ALTER TABLE public.contest_comparison_audit_disagreement OWNER TO corlaadmin;

--
-- Name: contest_comparison_audit_discrepancy; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.contest_comparison_audit_discrepancy (
    contest_comparison_audit_id bigint NOT NULL,
    discrepancy integer,
    cvr_audit_info_id bigint NOT NULL
);


ALTER TABLE public.contest_comparison_audit_discrepancy OWNER TO corlaadmin;

--
-- Name: contest_result; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.contest_result (
    id bigint NOT NULL,
    audit_reason integer,
    ballot_count bigint,
    contest_name character varying(255) NOT NULL,
    diluted_margin numeric(19,2),
    losers text,
    max_margin integer,
    min_margin integer,
    version bigint,
    winners text,
    winners_allowed integer
);


ALTER TABLE public.contest_result OWNER TO corlaadmin;

--
-- Name: contest_to_audit; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.contest_to_audit (
    dashboard_id bigint NOT NULL,
    audit character varying(255),
    contest_id bigint NOT NULL,
    reason character varying(255)
);


ALTER TABLE public.contest_to_audit OWNER TO corlaadmin;

--
-- Name: contest_vote_total; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.contest_vote_total (
    result_id bigint NOT NULL,
    vote_total integer,
    choice character varying(255) NOT NULL
);


ALTER TABLE public.contest_vote_total OWNER TO corlaadmin;

--
-- Name: contests_to_contest_results; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.contests_to_contest_results (
    contest_result_id bigint NOT NULL,
    contest_id bigint NOT NULL
);


ALTER TABLE public.contests_to_contest_results OWNER TO corlaadmin;

--
-- Name: counties_to_contest_results; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.counties_to_contest_results (
    contest_result_id bigint NOT NULL,
    county_id bigint NOT NULL
);


ALTER TABLE public.counties_to_contest_results OWNER TO corlaadmin;

--
-- Name: county; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.county (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    version bigint
);


ALTER TABLE public.county OWNER TO corlaadmin;

--
-- Name: county_contest_comparison_audit; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.county_contest_comparison_audit (
    id bigint NOT NULL,
    diluted_margin numeric(10,8) NOT NULL,
    audit_reason character varying(255) NOT NULL,
    audit_status character varying(255) NOT NULL,
    audited_sample_count integer NOT NULL,
    disagreement_count integer NOT NULL,
    estimated_recalculate_needed boolean NOT NULL,
    estimated_samples_to_audit integer NOT NULL,
    gamma numeric(10,8) NOT NULL,
    one_vote_over_count integer NOT NULL,
    one_vote_under_count integer NOT NULL,
    optimistic_recalculate_needed boolean NOT NULL,
    optimistic_samples_to_audit integer NOT NULL,
    other_count integer NOT NULL,
    risk_limit numeric(10,8) NOT NULL,
    two_vote_over_count integer NOT NULL,
    two_vote_under_count integer NOT NULL,
    version bigint,
    contest_id bigint NOT NULL,
    contest_result_id bigint NOT NULL,
    dashboard_id bigint NOT NULL
);


ALTER TABLE public.county_contest_comparison_audit OWNER TO corlaadmin;

--
-- Name: county_contest_comparison_audit_disagreement; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.county_contest_comparison_audit_disagreement (
    county_contest_comparison_audit_id bigint NOT NULL,
    cvr_audit_info_id bigint NOT NULL
);


ALTER TABLE public.county_contest_comparison_audit_disagreement OWNER TO corlaadmin;

--
-- Name: county_contest_comparison_audit_discrepancy; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.county_contest_comparison_audit_discrepancy (
    county_contest_comparison_audit_id bigint NOT NULL,
    discrepancy integer,
    cvr_audit_info_id bigint NOT NULL
);


ALTER TABLE public.county_contest_comparison_audit_discrepancy OWNER TO corlaadmin;

--
-- Name: county_contest_result; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.county_contest_result (
    id bigint NOT NULL,
    contest_ballot_count integer,
    county_ballot_count integer,
    losers text,
    max_margin integer,
    min_margin integer,
    version bigint,
    winners text,
    winners_allowed integer NOT NULL,
    contest_id bigint NOT NULL,
    county_id bigint NOT NULL
);


ALTER TABLE public.county_contest_result OWNER TO corlaadmin;

--
-- Name: county_contest_vote_total; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.county_contest_vote_total (
    result_id bigint NOT NULL,
    vote_total integer,
    choice character varying(255) NOT NULL
);


ALTER TABLE public.county_contest_vote_total OWNER TO corlaadmin;

--
-- Name: county_dashboard; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.county_dashboard (
    id bigint NOT NULL,
    audit_board_count integer,
    driving_contests text,
    audit_timestamp timestamp without time zone,
    audited_prefix_length integer,
    audited_sample_count integer,
    ballots_audited integer NOT NULL,
    ballots_in_manifest integer NOT NULL,
    current_round_index integer,
    cvr_import_error_message character varying(255),
    cvr_import_state character varying(255),
    cvr_import_timestamp timestamp without time zone,
    cvrs_imported integer NOT NULL,
    disagreements text NOT NULL,
    discrepancies text NOT NULL,
    version bigint,
    county_id bigint NOT NULL,
    cvr_file_id bigint,
    manifest_file_id bigint
);


ALTER TABLE public.county_dashboard OWNER TO corlaadmin;

--
-- Name: county_dashboard_to_comparison_audit; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.county_dashboard_to_comparison_audit (
    dashboard_id bigint NOT NULL,
    comparison_audit_id bigint NOT NULL
);


ALTER TABLE public.county_dashboard_to_comparison_audit OWNER TO corlaadmin;

--
-- Name: cvr_audit_info; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.cvr_audit_info (
    id bigint NOT NULL,
    count_by_contest text,
    multiplicity_by_contest text,
    disagreement text NOT NULL,
    discrepancy text NOT NULL,
    version bigint,
    acvr_id bigint,
    cvr_id bigint NOT NULL
);


ALTER TABLE public.cvr_audit_info OWNER TO corlaadmin;

--
-- Name: cvr_contest_info; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.cvr_contest_info (
    cvr_id bigint NOT NULL,
    county_id bigint,
    choices character varying(1024),
    comment character varying(255),
    consensus character varying(255),
    contest_id bigint NOT NULL,
    index integer NOT NULL
);


ALTER TABLE public.cvr_contest_info OWNER TO corlaadmin;

--
-- Name: dos_dashboard; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.dos_dashboard (
    id bigint NOT NULL,
    canonical_choices text,
    canonical_contests text,
    election_date timestamp without time zone,
    election_type character varying(255),
    public_meeting_date timestamp without time zone,
    risk_limit numeric(10,8),
    seed character varying(255),
    version bigint
);


ALTER TABLE public.dos_dashboard OWNER TO corlaadmin;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: corlaadmin
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.hibernate_sequence OWNER TO corlaadmin;

--
-- Name: log; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.log (
    id bigint NOT NULL,
    authentication_data character varying(255),
    client_host character varying(255),
    hash character varying(255) NOT NULL,
    information character varying(255) NOT NULL,
    result_code integer,
    "timestamp" timestamp without time zone NOT NULL,
    version bigint,
    previous_entry bigint
);


ALTER TABLE public.log OWNER TO corlaadmin;

--
-- Name: round; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.round (
    dashboard_id bigint NOT NULL,
    ballot_sequence_assignment text NOT NULL,
    actual_audited_prefix_length integer,
    actual_count integer NOT NULL,
    audit_subsequence text NOT NULL,
    ballot_sequence text NOT NULL,
    disagreements text NOT NULL,
    discrepancies text NOT NULL,
    end_time timestamp without time zone,
    expected_audited_prefix_length integer NOT NULL,
    expected_count integer NOT NULL,
    number integer NOT NULL,
    previous_ballots_audited integer NOT NULL,
    signatories text,
    start_audited_prefix_length integer NOT NULL,
    start_time timestamp without time zone NOT NULL,
    index integer NOT NULL
);


ALTER TABLE public.round OWNER TO corlaadmin;

--
-- Name: tribute; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.tribute (
    id bigint NOT NULL,
    ballot_position integer,
    batch_id character varying(255),
    contest_name character varying(255),
    county_id bigint,
    rand integer,
    rand_sequence_position integer,
    scanner_id integer,
    uri character varying(255),
    version bigint
);


ALTER TABLE public.tribute OWNER TO corlaadmin;

--
-- Name: uploaded_file; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.uploaded_file (
    id bigint NOT NULL,
    computed_hash character varying(255) NOT NULL,
    approximate_record_count integer NOT NULL,
    file oid NOT NULL,
    filename character varying(255),
    size bigint NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    version bigint,
    result text,
    status character varying(255) NOT NULL,
    submitted_hash character varying(255) NOT NULL,
    county_id bigint NOT NULL
);


ALTER TABLE public.uploaded_file OWNER TO corlaadmin;

--
-- Data for Name: administrator; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-1, 'State Administrator 1', NULL, NULL, 'STATE', 'stateadmin1', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-2, 'State Administrator 2', NULL, NULL, 'STATE', 'stateadmin2', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-3, 'State Administrator 3', NULL, NULL, 'STATE', 'stateadmin3', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-4, 'State Administrator 4', NULL, NULL, 'STATE', 'stateadmin4', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-5, 'State Administrator 5', NULL, NULL, 'STATE', 'stateadmin5', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-6, 'State Administrator 6', NULL, NULL, 'STATE', 'stateadmin6', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-7, 'State Administrator 7', NULL, NULL, 'STATE', 'stateadmin7', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-8, 'State Administrator 8', NULL, NULL, 'STATE', 'stateadmin8', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-104, 'County Administrator 4', NULL, NULL, 'COUNTY', 'countyadmin4', 0, 4);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-105, 'County Administrator 5', NULL, NULL, 'COUNTY', 'countyadmin5', 0, 5);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-106, 'County Administrator 6', NULL, NULL, 'COUNTY', 'countyadmin6', 0, 6);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-107, 'County Administrator 7', NULL, NULL, 'COUNTY', 'countyadmin7', 0, 7);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-108, 'County Administrator 8', NULL, NULL, 'COUNTY', 'countyadmin8', 0, 8);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-109, 'County Administrator 9', NULL, NULL, 'COUNTY', 'countyadmin9', 0, 9);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-110, 'County Administrator 10', NULL, NULL, 'COUNTY', 'countyadmin10', 0, 10);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-111, 'County Administrator 11', NULL, NULL, 'COUNTY', 'countyadmin11', 0, 11);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-112, 'County Administrator 12', NULL, NULL, 'COUNTY', 'countyadmin12', 0, 12);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-113, 'County Administrator 13', NULL, NULL, 'COUNTY', 'countyadmin13', 0, 13);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-114, 'County Administrator 14', NULL, NULL, 'COUNTY', 'countyadmin14', 0, 14);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-115, 'County Administrator 15', NULL, NULL, 'COUNTY', 'countyadmin15', 0, 15);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-116, 'County Administrator 16', NULL, NULL, 'COUNTY', 'countyadmin16', 0, 16);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-117, 'County Administrator 17', NULL, NULL, 'COUNTY', 'countyadmin17', 0, 17);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-118, 'County Administrator 18', NULL, NULL, 'COUNTY', 'countyadmin18', 0, 18);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-119, 'County Administrator 19', NULL, NULL, 'COUNTY', 'countyadmin19', 0, 19);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-120, 'County Administrator 20', NULL, NULL, 'COUNTY', 'countyadmin20', 0, 20);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-121, 'County Administrator 21', NULL, NULL, 'COUNTY', 'countyadmin21', 0, 21);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-122, 'County Administrator 22', NULL, NULL, 'COUNTY', 'countyadmin22', 0, 22);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-123, 'County Administrator 23', NULL, NULL, 'COUNTY', 'countyadmin23', 0, 23);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-124, 'County Administrator 24', NULL, NULL, 'COUNTY', 'countyadmin24', 0, 24);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-125, 'County Administrator 25', NULL, NULL, 'COUNTY', 'countyadmin25', 0, 25);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-126, 'County Administrator 26', NULL, NULL, 'COUNTY', 'countyadmin26', 0, 26);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-127, 'County Administrator 27', NULL, NULL, 'COUNTY', 'countyadmin27', 0, 27);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-128, 'County Administrator 28', NULL, NULL, 'COUNTY', 'countyadmin28', 0, 28);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-129, 'County Administrator 29', NULL, NULL, 'COUNTY', 'countyadmin29', 0, 29);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-130, 'County Administrator 30', NULL, NULL, 'COUNTY', 'countyadmin30', 0, 30);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-131, 'County Administrator 31', NULL, NULL, 'COUNTY', 'countyadmin31', 0, 31);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-132, 'County Administrator 32', NULL, NULL, 'COUNTY', 'countyadmin32', 0, 32);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-133, 'County Administrator 33', NULL, NULL, 'COUNTY', 'countyadmin33', 0, 33);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-134, 'County Administrator 34', NULL, NULL, 'COUNTY', 'countyadmin34', 0, 34);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-135, 'County Administrator 35', NULL, NULL, 'COUNTY', 'countyadmin35', 0, 35);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-136, 'County Administrator 36', NULL, NULL, 'COUNTY', 'countyadmin36', 0, 36);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-137, 'County Administrator 37', NULL, NULL, 'COUNTY', 'countyadmin37', 0, 37);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-138, 'County Administrator 38', NULL, NULL, 'COUNTY', 'countyadmin38', 0, 38);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-139, 'County Administrator 39', NULL, NULL, 'COUNTY', 'countyadmin39', 0, 39);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-140, 'County Administrator 40', NULL, NULL, 'COUNTY', 'countyadmin40', 0, 40);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-141, 'County Administrator 41', NULL, NULL, 'COUNTY', 'countyadmin41', 0, 41);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-142, 'County Administrator 42', NULL, NULL, 'COUNTY', 'countyadmin42', 0, 42);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-143, 'County Administrator 43', NULL, NULL, 'COUNTY', 'countyadmin43', 0, 43);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-144, 'County Administrator 44', NULL, NULL, 'COUNTY', 'countyadmin44', 0, 44);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-145, 'County Administrator 45', NULL, NULL, 'COUNTY', 'countyadmin45', 0, 45);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-146, 'County Administrator 46', NULL, NULL, 'COUNTY', 'countyadmin46', 0, 46);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-147, 'County Administrator 47', NULL, NULL, 'COUNTY', 'countyadmin47', 0, 47);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-148, 'County Administrator 48', NULL, NULL, 'COUNTY', 'countyadmin48', 0, 48);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-149, 'County Administrator 49', NULL, NULL, 'COUNTY', 'countyadmin49', 0, 49);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-150, 'County Administrator 10', NULL, NULL, 'COUNTY', 'countyadmin50', 0, 50);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-151, 'County Administrator 51', NULL, NULL, 'COUNTY', 'countyadmin51', 0, 51);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-152, 'County Administrator 52', NULL, NULL, 'COUNTY', 'countyadmin52', 0, 52);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-153, 'County Administrator 53', NULL, NULL, 'COUNTY', 'countyadmin53', 0, 53);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-154, 'County Administrator 54', NULL, NULL, 'COUNTY', 'countyadmin54', 0, 54);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-155, 'County Administrator 55', NULL, NULL, 'COUNTY', 'countyadmin55', 0, 55);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-156, 'County Administrator 56', NULL, NULL, 'COUNTY', 'countyadmin56', 0, 56);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-157, 'County Administrator 57', NULL, NULL, 'COUNTY', 'countyadmin57', 0, 57);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-158, 'County Administrator 58', NULL, NULL, 'COUNTY', 'countyadmin58', 0, 58);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-159, 'County Administrator 59', NULL, NULL, 'COUNTY', 'countyadmin59', 0, 59);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-160, 'County Administrator 60', NULL, NULL, 'COUNTY', 'countyadmin60', 0, 60);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-161, 'County Administrator 61', NULL, NULL, 'COUNTY', 'countyadmin61', 0, 61);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-162, 'County Administrator 62', NULL, NULL, 'COUNTY', 'countyadmin62', 0, 62);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-163, 'County Administrator 63', NULL, NULL, 'COUNTY', 'countyadmin63', 0, 63);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-164, 'County Administrator 64', NULL, NULL, 'COUNTY', 'countyadmin64', 0, 64);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-103, 'County Administrator 3', '2024-06-13 19:44:44.313229', NULL, 'COUNTY', 'countyadmin3', 2, 3);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-101, 'County Administrator 1', '2024-06-13 19:38:11.741514', '2024-06-13 19:43:40.577512', 'COUNTY', 'countyadmin1', 3, 1);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-102, 'County Administrator 2', '2024-06-13 19:43:49.975186', '2024-06-13 19:44:36.851499', 'COUNTY', 'countyadmin2', 3, 2);


--
-- Data for Name: asm_state; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240129, 'us.freeandfair.corla.asm.DoSDashboardASM', 'DoS', 'us.freeandfair.corla.asm.ASMState$DoSDashboardState', 'DOS_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240130, 'us.freeandfair.corla.asm.CountyDashboardASM', '44', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240131, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '44', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240132, 'us.freeandfair.corla.asm.CountyDashboardASM', '45', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240133, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '45', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240134, 'us.freeandfair.corla.asm.CountyDashboardASM', '46', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240135, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '46', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240136, 'us.freeandfair.corla.asm.CountyDashboardASM', '47', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240137, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '47', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240138, 'us.freeandfair.corla.asm.CountyDashboardASM', '48', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240139, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '48', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240140, 'us.freeandfair.corla.asm.CountyDashboardASM', '49', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240141, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '49', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240142, 'us.freeandfair.corla.asm.CountyDashboardASM', '50', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240143, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '50', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240144, 'us.freeandfair.corla.asm.CountyDashboardASM', '51', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240145, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '51', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240146, 'us.freeandfair.corla.asm.CountyDashboardASM', '52', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240147, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '52', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240148, 'us.freeandfair.corla.asm.CountyDashboardASM', '53', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240149, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '53', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240150, 'us.freeandfair.corla.asm.CountyDashboardASM', '10', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240151, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '10', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240152, 'us.freeandfair.corla.asm.CountyDashboardASM', '54', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240153, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '54', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240154, 'us.freeandfair.corla.asm.CountyDashboardASM', '11', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240155, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '11', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240156, 'us.freeandfair.corla.asm.CountyDashboardASM', '55', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240157, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '55', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240158, 'us.freeandfair.corla.asm.CountyDashboardASM', '12', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240159, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '12', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240160, 'us.freeandfair.corla.asm.CountyDashboardASM', '56', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240161, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '56', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240162, 'us.freeandfair.corla.asm.CountyDashboardASM', '13', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240163, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '13', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240164, 'us.freeandfair.corla.asm.CountyDashboardASM', '57', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240165, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '57', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240166, 'us.freeandfair.corla.asm.CountyDashboardASM', '14', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240167, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '14', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240168, 'us.freeandfair.corla.asm.CountyDashboardASM', '58', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240169, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '58', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240170, 'us.freeandfair.corla.asm.CountyDashboardASM', '15', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240171, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '15', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240172, 'us.freeandfair.corla.asm.CountyDashboardASM', '59', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240173, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '59', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240174, 'us.freeandfair.corla.asm.CountyDashboardASM', '16', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240175, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '16', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240176, 'us.freeandfair.corla.asm.CountyDashboardASM', '17', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240177, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '17', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240178, 'us.freeandfair.corla.asm.CountyDashboardASM', '18', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240179, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '18', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240180, 'us.freeandfair.corla.asm.CountyDashboardASM', '19', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240181, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '19', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240183, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '1', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240185, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '2', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240187, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '3', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240188, 'us.freeandfair.corla.asm.CountyDashboardASM', '4', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240189, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '4', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240190, 'us.freeandfair.corla.asm.CountyDashboardASM', '5', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240191, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '5', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240192, 'us.freeandfair.corla.asm.CountyDashboardASM', '6', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240193, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '6', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240194, 'us.freeandfair.corla.asm.CountyDashboardASM', '7', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240195, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '7', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240196, 'us.freeandfair.corla.asm.CountyDashboardASM', '8', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240197, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '8', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240198, 'us.freeandfair.corla.asm.CountyDashboardASM', '9', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240199, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '9', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240200, 'us.freeandfair.corla.asm.CountyDashboardASM', '60', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240201, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '60', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240202, 'us.freeandfair.corla.asm.CountyDashboardASM', '61', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240203, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '61', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240204, 'us.freeandfair.corla.asm.CountyDashboardASM', '62', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240205, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '62', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240206, 'us.freeandfair.corla.asm.CountyDashboardASM', '63', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240207, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '63', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240208, 'us.freeandfair.corla.asm.CountyDashboardASM', '20', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240209, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '20', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240210, 'us.freeandfair.corla.asm.CountyDashboardASM', '64', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240211, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '64', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240212, 'us.freeandfair.corla.asm.CountyDashboardASM', '21', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240213, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '21', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240214, 'us.freeandfair.corla.asm.CountyDashboardASM', '22', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240215, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '22', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240216, 'us.freeandfair.corla.asm.CountyDashboardASM', '23', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240217, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '23', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240218, 'us.freeandfair.corla.asm.CountyDashboardASM', '24', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240219, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '24', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240220, 'us.freeandfair.corla.asm.CountyDashboardASM', '25', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240184, 'us.freeandfair.corla.asm.CountyDashboardASM', '2', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'BALLOT_MANIFEST_AND_CVRS_OK', 3);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240186, 'us.freeandfair.corla.asm.CountyDashboardASM', '3', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'BALLOT_MANIFEST_AND_CVRS_OK', 5);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240221, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '25', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240222, 'us.freeandfair.corla.asm.CountyDashboardASM', '26', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240223, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '26', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240224, 'us.freeandfair.corla.asm.CountyDashboardASM', '27', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240225, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '27', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240226, 'us.freeandfair.corla.asm.CountyDashboardASM', '28', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240227, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '28', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240228, 'us.freeandfair.corla.asm.CountyDashboardASM', '29', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240229, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '29', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240230, 'us.freeandfair.corla.asm.CountyDashboardASM', '30', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240231, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '30', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240232, 'us.freeandfair.corla.asm.CountyDashboardASM', '31', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240233, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '31', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240234, 'us.freeandfair.corla.asm.CountyDashboardASM', '32', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240235, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '32', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240236, 'us.freeandfair.corla.asm.CountyDashboardASM', '33', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240237, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '33', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240238, 'us.freeandfair.corla.asm.CountyDashboardASM', '34', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240239, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '34', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240240, 'us.freeandfair.corla.asm.CountyDashboardASM', '35', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240241, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '35', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240242, 'us.freeandfair.corla.asm.CountyDashboardASM', '36', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240243, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '36', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240244, 'us.freeandfair.corla.asm.CountyDashboardASM', '37', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240245, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '37', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240246, 'us.freeandfair.corla.asm.CountyDashboardASM', '38', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240247, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '38', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240248, 'us.freeandfair.corla.asm.CountyDashboardASM', '39', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240249, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '39', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240250, 'us.freeandfair.corla.asm.CountyDashboardASM', '40', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240251, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '40', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240252, 'us.freeandfair.corla.asm.CountyDashboardASM', '41', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240253, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '41', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240254, 'us.freeandfair.corla.asm.CountyDashboardASM', '42', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240255, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '42', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240256, 'us.freeandfair.corla.asm.CountyDashboardASM', '43', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240257, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '43', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (240182, 'us.freeandfair.corla.asm.CountyDashboardASM', '1', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'BALLOT_MANIFEST_AND_CVRS_OK', 5);


--
-- Data for Name: audit_board; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: audit_intermediate_report; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: audit_investigation_report; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: ballot_manifest_info; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.ballot_manifest_info (id, batch_id, batch_size, county_id, scanner_id, sequence_end, sequence_start, storage_location, version, ultimate_sequence_end, ultimate_sequence_start, uri) VALUES (240548, '1', 10, 1, 1, 10, 1, 'Bin 1', 0, NULL, NULL, 'bmi:1:1-1');
INSERT INTO public.ballot_manifest_info (id, batch_id, batch_size, county_id, scanner_id, sequence_end, sequence_start, storage_location, version, ultimate_sequence_end, ultimate_sequence_start, uri) VALUES (240595, '1', 10, 2, 1, 10, 1, 'Bin 1', 0, NULL, NULL, 'bmi:2:1-1');
INSERT INTO public.ballot_manifest_info (id, batch_id, batch_size, county_id, scanner_id, sequence_end, sequence_start, storage_location, version, ultimate_sequence_end, ultimate_sequence_start, uri) VALUES (241100, '1', 10, 3, 1, 10, 1, 'Bin 1', 0, NULL, NULL, 'bmi:3:1-1');


--
-- Data for Name: cast_vote_record; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240509, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 1, '1-1-1', 1, 'UPLOADED', 1, 0, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-1');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240510, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 2, '1-1-2', 2, 'UPLOADED', 1, 1, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-2');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240511, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 3, '1-1-3', 3, 'UPLOADED', 1, 2, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-3');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240512, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 4, '1-1-4', 4, 'UPLOADED', 1, 3, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-4');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240513, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 5, '1-1-5', 5, 'UPLOADED', 1, 4, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-5');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240514, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 6, '1-1-6', 6, 'UPLOADED', 1, 5, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-6');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240515, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 7, '1-1-7', 7, 'UPLOADED', 1, 6, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-7');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240516, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 8, '1-1-8', 8, 'UPLOADED', 1, 7, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-8');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240517, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 9, '1-1-9', 9, 'UPLOADED', 1, 8, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-9');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240518, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 1, 10, '1-1-10', 10, 'UPLOADED', 1, 9, NULL, 0, NULL, NULL, NULL, 'cvr:1:1-1-10');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240631, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 1, '1-1-1', 1, 'UPLOADED', 1, 0, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-1');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240632, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 2, '1-1-2', 2, 'UPLOADED', 1, 1, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-2');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240633, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 3, '1-1-3', 3, 'UPLOADED', 1, 2, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-3');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240634, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 4, '1-1-4', 4, 'UPLOADED', 1, 3, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-4');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240635, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 5, '1-1-5', 5, 'UPLOADED', 1, 4, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-5');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240636, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 6, '1-1-6', 6, 'UPLOADED', 1, 5, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-6');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240637, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 7, '1-1-7', 7, 'UPLOADED', 1, 6, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-7');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240638, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 8, '1-1-8', 8, 'UPLOADED', 1, 7, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-8');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240639, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 9, '1-1-9', 9, 'UPLOADED', 1, 8, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-9');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (240640, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 10, '1-1-10', 10, 'UPLOADED', 1, 9, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-10');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241057, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 1, '1-1-1', 1, 'UPLOADED', 1, 0, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-1');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241058, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 2, '1-1-2', 2, 'UPLOADED', 1, 1, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-2');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241059, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 3, '1-1-3', 3, 'UPLOADED', 1, 2, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-3');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241060, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 4, '1-1-4', 4, 'UPLOADED', 1, 3, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-4');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241061, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 5, '1-1-5', 5, 'UPLOADED', 1, 4, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-5');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241062, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 6, '1-1-6', 6, 'UPLOADED', 1, 5, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-6');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241063, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 7, '1-1-7', 7, 'UPLOADED', 1, 6, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-7');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241064, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 8, '1-1-8', 8, 'UPLOADED', 1, 7, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-8');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241065, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 9, '1-1-9', 9, 'UPLOADED', 1, 8, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-9');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (241066, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 10, '1-1-10', 10, 'UPLOADED', 1, 9, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-10');


--
-- Data for Name: comparison_audit; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contest; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (240503, 'IRV', 'TinyExample1', 0, 0, 3, 1, 1);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (240505, 'PLURALITY', 'PluralityExample1', 1, 0, 1, 1, 1);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (240507, 'PLURALITY', 'PluralityExample2', 2, 0, 2, 2, 1);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (240629, 'IRV', 'TinyExample1', 0, 0, 3, 1, 2);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (241051, 'IRV', 'TinyExample1', 0, 0, 3, 1, 3);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (241053, 'PLURALITY', 'PluralityExample1', 1, 0, 1, 1, 3);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (241055, 'IRV', 'PluralityExample2', 2, 0, 3, 1, 3);


--
-- Data for Name: contest_choice; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240503, '', false, 'Alice', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240503, '', false, 'Bob', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240503, '', false, 'Chuan', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240505, '', false, 'Diego', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240505, '', false, 'Eli', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240505, '', false, 'Farhad', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240507, '', false, 'Gertrude', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240507, '', false, 'Ho', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240507, '', false, 'Imogen', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240629, '', false, 'Alice', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240629, '', false, 'Bob', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (240629, '', false, 'Chuan', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241051, '', false, 'Alice', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241051, '', false, 'Bob', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241051, '', false, 'Chuan', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241053, '', false, 'Diego', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241053, '', false, 'Eli', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241053, '', false, 'Farhad', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241055, '', false, 'Gertrude', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241055, '', false, 'Ho', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241055, '', false, 'Imogen', false, 2);


--
-- Data for Name: contest_comparison_audit_disagreement; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contest_comparison_audit_discrepancy; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contest_result; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contest_to_audit; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contest_vote_total; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contests_to_contest_results; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: counties_to_contest_results; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: county; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.county (id, name, version) VALUES (44, 'Morgan', 0);
INSERT INTO public.county (id, name, version) VALUES (45, 'Otero', 0);
INSERT INTO public.county (id, name, version) VALUES (46, 'Ouray', 0);
INSERT INTO public.county (id, name, version) VALUES (47, 'Park', 0);
INSERT INTO public.county (id, name, version) VALUES (48, 'Phillips', 0);
INSERT INTO public.county (id, name, version) VALUES (49, 'Pitkin', 0);
INSERT INTO public.county (id, name, version) VALUES (50, 'Prowers', 0);
INSERT INTO public.county (id, name, version) VALUES (51, 'Pueblo', 0);
INSERT INTO public.county (id, name, version) VALUES (52, 'Rio Blanco', 0);
INSERT INTO public.county (id, name, version) VALUES (53, 'Rio Grande', 0);
INSERT INTO public.county (id, name, version) VALUES (10, 'Clear Creek', 0);
INSERT INTO public.county (id, name, version) VALUES (54, 'Routt', 0);
INSERT INTO public.county (id, name, version) VALUES (11, 'Conejos', 0);
INSERT INTO public.county (id, name, version) VALUES (55, 'Saguache', 0);
INSERT INTO public.county (id, name, version) VALUES (12, 'Costilla', 0);
INSERT INTO public.county (id, name, version) VALUES (56, 'San Juan', 0);
INSERT INTO public.county (id, name, version) VALUES (13, 'Crowley', 0);
INSERT INTO public.county (id, name, version) VALUES (57, 'San Miguel', 0);
INSERT INTO public.county (id, name, version) VALUES (14, 'Custer', 0);
INSERT INTO public.county (id, name, version) VALUES (58, 'Sedgwick', 0);
INSERT INTO public.county (id, name, version) VALUES (15, 'Delta', 0);
INSERT INTO public.county (id, name, version) VALUES (59, 'Summit', 0);
INSERT INTO public.county (id, name, version) VALUES (16, 'Denver', 0);
INSERT INTO public.county (id, name, version) VALUES (17, 'Dolores', 0);
INSERT INTO public.county (id, name, version) VALUES (18, 'Douglas', 0);
INSERT INTO public.county (id, name, version) VALUES (19, 'Eagle', 0);
INSERT INTO public.county (id, name, version) VALUES (1, 'Adams', 0);
INSERT INTO public.county (id, name, version) VALUES (2, 'Alamosa', 0);
INSERT INTO public.county (id, name, version) VALUES (3, 'Arapahoe', 0);
INSERT INTO public.county (id, name, version) VALUES (4, 'Archuleta', 0);
INSERT INTO public.county (id, name, version) VALUES (5, 'Baca', 0);
INSERT INTO public.county (id, name, version) VALUES (6, 'Bent', 0);
INSERT INTO public.county (id, name, version) VALUES (7, 'Boulder', 0);
INSERT INTO public.county (id, name, version) VALUES (8, 'Chaffee', 0);
INSERT INTO public.county (id, name, version) VALUES (9, 'Cheyenne', 0);
INSERT INTO public.county (id, name, version) VALUES (60, 'Teller', 0);
INSERT INTO public.county (id, name, version) VALUES (61, 'Washington', 0);
INSERT INTO public.county (id, name, version) VALUES (62, 'Weld', 0);
INSERT INTO public.county (id, name, version) VALUES (63, 'Yuma', 0);
INSERT INTO public.county (id, name, version) VALUES (20, 'Elbert', 0);
INSERT INTO public.county (id, name, version) VALUES (64, 'Broomfield', 0);
INSERT INTO public.county (id, name, version) VALUES (21, 'El Paso', 0);
INSERT INTO public.county (id, name, version) VALUES (22, 'Fremont', 0);
INSERT INTO public.county (id, name, version) VALUES (23, 'Garfield', 0);
INSERT INTO public.county (id, name, version) VALUES (24, 'Gilpin', 0);
INSERT INTO public.county (id, name, version) VALUES (25, 'Grand', 0);
INSERT INTO public.county (id, name, version) VALUES (26, 'Gunnison', 0);
INSERT INTO public.county (id, name, version) VALUES (27, 'Hinsdale', 0);
INSERT INTO public.county (id, name, version) VALUES (28, 'Huerfano', 0);
INSERT INTO public.county (id, name, version) VALUES (29, 'Jackson', 0);
INSERT INTO public.county (id, name, version) VALUES (30, 'Jefferson', 0);
INSERT INTO public.county (id, name, version) VALUES (31, 'Kiowa', 0);
INSERT INTO public.county (id, name, version) VALUES (32, 'Kit Carson', 0);
INSERT INTO public.county (id, name, version) VALUES (33, 'Lake', 0);
INSERT INTO public.county (id, name, version) VALUES (34, 'La Plata', 0);
INSERT INTO public.county (id, name, version) VALUES (35, 'Larimer', 0);
INSERT INTO public.county (id, name, version) VALUES (36, 'Las Animas', 0);
INSERT INTO public.county (id, name, version) VALUES (37, 'Lincoln', 0);
INSERT INTO public.county (id, name, version) VALUES (38, 'Logan', 0);
INSERT INTO public.county (id, name, version) VALUES (39, 'Mesa', 0);
INSERT INTO public.county (id, name, version) VALUES (40, 'Mineral', 0);
INSERT INTO public.county (id, name, version) VALUES (41, 'Moffat', 0);
INSERT INTO public.county (id, name, version) VALUES (42, 'Montezuma', 0);
INSERT INTO public.county (id, name, version) VALUES (43, 'Montrose', 0);


--
-- Data for Name: county_contest_comparison_audit; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: county_contest_comparison_audit_disagreement; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: county_contest_comparison_audit_discrepancy; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: county_contest_result; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (240504, 10, 10, '["Alice","Chuan"]', 0, 0, 1, '["Bob"]', 1, 240503, 1);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (240506, 10, 10, '["Eli","Farhad"]', 1, 1, 1, '["Diego"]', 1, 240505, 1);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (240508, 10, 10, '["Imogen"]', 7, 6, 1, '["Ho","Gertrude"]', 2, 240507, 1);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (240630, 10, 10, '["Alice","Chuan"]', 0, 0, 1, '["Bob"]', 1, 240629, 2);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (241052, 10, 10, '["Alice","Chuan"]', 0, 0, 1, '["Bob"]', 1, 241051, 3);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (241054, 10, 10, '["Eli","Farhad"]', 1, 1, 1, '["Diego"]', 1, 241053, 3);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (241056, 10, 10, '["Ho","Gertrude"]', 0, 0, 1, '["Imogen"]', 1, 241055, 3);


--
-- Data for Name: county_contest_vote_total; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240504, 10, 'Bob');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240504, 10, 'Alice');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240504, 10, 'Chuan');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240506, 3, 'Eli');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240506, 4, 'Diego');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240506, 3, 'Farhad');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240508, 2, 'Imogen');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240508, 8, 'Ho');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240508, 9, 'Gertrude');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240630, 10, 'Bob');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240630, 10, 'Alice');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (240630, 10, 'Chuan');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241052, 10, 'Bob');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241052, 10, 'Alice');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241052, 10, 'Chuan');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241054, 3, 'Eli');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241054, 4, 'Diego');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241054, 3, 'Farhad');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241056, 10, 'Imogen');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241056, 10, 'Ho');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (241056, 10, 'Gertrude');


--
-- Data for Name: county_dashboard; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (44, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.152132', 0, '{}', '{}', 0, 44, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (45, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.170835', 0, '{}', '{}', 0, 45, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (46, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.182083', 0, '{}', '{}', 0, 46, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (47, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.193728', 0, '{}', '{}', 0, 47, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (48, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.203992', 0, '{}', '{}', 0, 48, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (49, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.217359', 0, '{}', '{}', 0, 49, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (50, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.235268', 0, '{}', '{}', 0, 50, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (51, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.258784', 0, '{}', '{}', 0, 51, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (52, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.272207', 0, '{}', '{}', 0, 52, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (53, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.283146', 0, '{}', '{}', 0, 53, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (10, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.293707', 0, '{}', '{}', 0, 10, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (54, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.301724', 0, '{}', '{}', 0, 54, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (11, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.310832', 0, '{}', '{}', 0, 11, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (55, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.319739', 0, '{}', '{}', 0, 55, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (12, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.328974', 0, '{}', '{}', 0, 12, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (56, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.338243', 0, '{}', '{}', 0, 56, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (13, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.346386', 0, '{}', '{}', 0, 13, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (57, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.355365', 0, '{}', '{}', 0, 57, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (14, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.364679', 0, '{}', '{}', 0, 14, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (58, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.373442', 0, '{}', '{}', 0, 58, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (15, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.382407', 0, '{}', '{}', 0, 15, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (59, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.393561', 0, '{}', '{}', 0, 59, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (16, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.408555', 0, '{}', '{}', 0, 16, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (17, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.418009', 0, '{}', '{}', 0, 17, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (18, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.425478', 0, '{}', '{}', 0, 18, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (19, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.434761', 0, '{}', '{}', 0, 19, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (4, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.494769', 0, '{}', '{}', 0, 4, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (5, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.50265', 0, '{}', '{}', 0, 5, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (6, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.51555', 0, '{}', '{}', 0, 6, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (7, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.525238', 0, '{}', '{}', 0, 7, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (8, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.535455', 0, '{}', '{}', 0, 8, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (9, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.551404', 0, '{}', '{}', 0, 9, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (60, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.564153', 0, '{}', '{}', 0, 60, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (61, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.578099', 0, '{}', '{}', 0, 61, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (62, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.594047', 0, '{}', '{}', 0, 62, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (63, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.603073', 0, '{}', '{}', 0, 63, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (20, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.613887', 0, '{}', '{}', 0, 20, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (64, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.623118', 0, '{}', '{}', 0, 64, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (21, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.633271', 0, '{}', '{}', 0, 21, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (22, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.643226', 0, '{}', '{}', 0, 22, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (23, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.656257', 0, '{}', '{}', 0, 23, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (24, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.665705', 0, '{}', '{}', 0, 24, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (25, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.674713', 0, '{}', '{}', 0, 25, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (26, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.687242', 0, '{}', '{}', 0, 26, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (27, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.700545', 0, '{}', '{}', 0, 27, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (28, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.70917', 0, '{}', '{}', 0, 28, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (29, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.717651', 0, '{}', '{}', 0, 29, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (30, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.726345', 0, '{}', '{}', 0, 30, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (31, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.735149', 0, '{}', '{}', 0, 31, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (32, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.745589', 0, '{}', '{}', 0, 32, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (33, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.759174', 0, '{}', '{}', 0, 33, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (34, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.769483', 0, '{}', '{}', 0, 34, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (35, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.780058', 0, '{}', '{}', 0, 35, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (36, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.789863', 0, '{}', '{}', 0, 36, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (37, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.798861', 0, '{}', '{}', 0, 37, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (38, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.808333', 0, '{}', '{}', 0, 38, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (39, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.820974', 0, '{}', '{}', 0, 39, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (40, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.832358', 0, '{}', '{}', 0, 40, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (41, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.840961', 0, '{}', '{}', 0, 41, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (42, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.85083', 0, '{}', '{}', 0, 42, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (43, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-13 19:25:50.867455', 0, '{}', '{}', 0, 43, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (1, NULL, '[]', NULL, NULL, NULL, 0, 10, NULL, NULL, 'SUCCESSFUL', '2024-06-13 19:42:50.135501', 10, '{}', '{}', 7, 1, 240488, 240543);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (2, NULL, '[]', NULL, NULL, NULL, 0, 10, NULL, NULL, 'SUCCESSFUL', '2024-06-13 19:44:28.219148', 10, '{}', '{}', 4, 2, 240618, 240590);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (3, NULL, '[]', NULL, NULL, NULL, 0, 10, NULL, NULL, 'SUCCESSFUL', '2024-06-13 19:53:12.895727', 10, '{}', '{}', 7, 3, 241040, 241095);


--
-- Data for Name: county_dashboard_to_comparison_audit; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: cvr_audit_info; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: cvr_contest_info; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240509, 1, '["Alice","Bob","Chuan"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240509, 1, '["Diego"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240509, 1, '["Gertrude","Imogen"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240510, 1, '["Alice","Bob","Chuan"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240510, 1, '["Farhad"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240510, 1, '["Imogen"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240511, 1, '["Alice","Bob","Chuan"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240511, 1, '["Farhad"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240511, 1, '["Gertrude","Ho"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240512, 1, '["Alice","Chuan","Bob"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240512, 1, '["Farhad"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240512, 1, '["Gertrude","Ho"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240513, 1, '["Alice","Chuan","Bob"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240513, 1, '["Diego"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240513, 1, '["Gertrude","Ho"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240514, 1, '["Alice","Chuan","Bob"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240514, 1, '["Diego"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240514, 1, '["Gertrude","Ho"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240515, 1, '["Bob","Alice","Chuan"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240515, 1, '["Diego"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240515, 1, '["Gertrude","Ho"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240516, 1, '["Chuan","Alice","Bob"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240516, 1, '["Eli"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240516, 1, '["Gertrude","Ho"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240517, 1, '["Chuan","Alice","Bob"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240517, 1, '["Eli"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240517, 1, '["Gertrude","Ho"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240518, 1, '["Chuan","Alice","Bob"]', NULL, NULL, 240503, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240518, 1, '["Eli"]', NULL, NULL, 240505, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240518, 1, '["Gertrude","Ho"]', NULL, NULL, 240507, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240631, 2, '["Alice","Bob","Chuan"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240632, 2, '["Alice","Bob","Chuan"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240633, 2, '["Alice","Bob","Chuan"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240634, 2, '["Alice","Chuan","Bob"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240635, 2, '["Alice","Chuan","Bob"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240636, 2, '["Alice","Chuan","Bob"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240637, 2, '["Bob","Alice","Chuan"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240638, 2, '["Chuan","Alice","Bob"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240639, 2, '["Chuan","Alice","Bob"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (240640, 2, '["Chuan","Alice","Bob"]', NULL, NULL, 240629, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241057, 3, '["Alice","Bob","Chuan"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241057, 3, '["Diego"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241057, 3, '["Gertrude","Ho","Imogen"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241058, 3, '["Alice","Bob","Chuan"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241058, 3, '["Farhad"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241058, 3, '["Imogen","Ho","Gertrude"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241059, 3, '["Alice","Bob","Chuan"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241059, 3, '["Farhad"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241059, 3, '["Ho","Imogen","Gertrude"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241060, 3, '["Alice","Chuan","Bob"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241060, 3, '["Farhad"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241060, 3, '["Gertrude","Imogen","Ho"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241061, 3, '["Alice","Chuan","Bob"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241061, 3, '["Diego"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241061, 3, '["Gertrude","Imogen","Ho"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241062, 3, '["Alice","Chuan","Bob"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241062, 3, '["Diego"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241062, 3, '["Ho","Imogen","Gertrude"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241063, 3, '["Bob","Alice","Chuan"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241063, 3, '["Diego"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241063, 3, '["Ho","Imogen","Gertrude"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241064, 3, '["Chuan","Alice","Bob"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241064, 3, '["Eli"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241064, 3, '["Ho","Imogen","Gertrude"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241065, 3, '["Chuan","Alice","Bob"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241065, 3, '["Eli"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241065, 3, '["Gertrude","Imogen","Ho"]', NULL, NULL, 241055, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241066, 3, '["Chuan","Alice","Bob"]', NULL, NULL, 241051, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241066, 3, '["Eli"]', NULL, NULL, 241053, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (241066, 3, '["Gertrude","Imogen","Ho"]', NULL, NULL, 241055, 2);


--
-- Data for Name: dos_dashboard; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.dos_dashboard (id, canonical_choices, canonical_contests, election_date, election_type, public_meeting_date, risk_limit, seed, version) VALUES (0, '{}', '{}', NULL, NULL, NULL, NULL, NULL, 0);


--
-- Data for Name: log; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240258, 'countyadmin1', 'localhost:8888', 'F1F0BFFFCD923D9ED192CCB01715F5E8601A375098223E0F9588D76F71A64ED5', '/auth-admin', 200, '2024-06-13 19:38:06.160378', 0, NULL);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240259, 'countyadmin1', 'localhost:8888', 'DD0AE7F6065A0CB5234E884221DFCCA3B16BD3CD853612319BCCB4519EE2596B', '/auth-admin', 200, '2024-06-13 19:38:11.745174', 0, 240258);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240260, 'countyadmin1', 'localhost:8888', '751BD3D5DB66BAF895C665D8E3F1F60824698BA7B01F03F311EF0CA987D004ED', '/audit-board-asm-state', 200, '2024-06-13 19:38:11.824831', 0, 240259);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240261, 'countyadmin1', 'localhost:8888', 'F4C828F1FCCB376A1DF3F8B108869BFBB041A2EA1AB50FEB246FCFBB3345A753', '/county-asm-state', 200, '2024-06-13 19:38:11.825101', 0, 240259);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240262, 'countyadmin1', 'localhost:8888', 'D24309B0FE41E44B3CC95300FADF49D6AE86997086E9A7EFFC376C861B22C35F', '/county-dashboard', 200, '2024-06-13 19:38:11.877433', 0, 240259);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240263, 'countyadmin1', 'localhost:8888', '6FEC144D24EADED37BACD2FAEB42AA70B49BA26FF9C2B5A1C8BF6562A3A4BED5', '/contest/county', 200, '2024-06-13 19:38:11.944174', 0, 240262);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240264, 'countyadmin1', 'localhost:8888', '9FAD9973ED94FF5FF335388C019F0075FD0EE8E91AB25F364F39D23F5155FA54', '/audit-board-asm-state', 200, '2024-06-13 19:38:16.949232', 0, 240263);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240265, 'countyadmin1', 'localhost:8888', '6ED39B11C83F41D4884A10636AC5FE3EFB34726859888A29E727ED781E664D7A', '/county-asm-state', 200, '2024-06-13 19:38:16.950108', 0, 240263);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240266, 'countyadmin1', 'localhost:8888', '1FBC7357C844DA4953D776EBBBCB9213112E9C45A6EC88AF1953827C0C16CDB7', '/county-dashboard', 200, '2024-06-13 19:38:16.969038', 0, 240263);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240267, 'countyadmin1', 'localhost:8888', 'E63E48965D20D37BB425306C2BD7AC7998C6C3D61795437F4D95E5BF4FCB5DAF', '/contest/county', 200, '2024-06-13 19:38:17.027523', 0, 240266);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240268, 'countyadmin1', 'localhost:8888', 'C426DB2665F2D5C1878309414F9523D6576039926B7149F46EFD4B4354A2AA0E', '/audit-board-asm-state', 200, '2024-06-13 19:38:22.04182', 0, 240267);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240269, 'countyadmin1', 'localhost:8888', '334B2085C77027D08F418458CCA4E1C040A39AAEB78271D06D8C334BFBDE0386', '/county-asm-state', 200, '2024-06-13 19:38:22.042716', 0, 240267);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240270, 'countyadmin1', 'localhost:8888', '677DDAC9340BBE1917B0E15D0F83D9BE069CEC674132DB45514B4CE3610EB022', '/county-dashboard', 200, '2024-06-13 19:38:22.063021', 0, 240267);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240271, 'countyadmin1', 'localhost:8888', '6E2F1F5D4036452AF0DD2A3D4D4D4A45F0A126F667AAC3B6CECA029780E2EBFF', '/contest/county', 200, '2024-06-13 19:38:22.120554', 0, 240270);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240272, 'countyadmin1', 'localhost:8888', 'CED9FB94E2D84E58CB74FFC68B6B2B5F111A14C388DCC9243F58DB98F50E6A70', '/audit-board-asm-state', 200, '2024-06-13 19:38:27.121072', 0, 240271);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240273, 'countyadmin1', 'localhost:8888', '8881FE923025C7B8AC1467E586923E39277F194EBCD5CAD26278E5A98018AB80', '/county-asm-state', 200, '2024-06-13 19:38:27.122632', 0, 240271);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240274, 'countyadmin1', 'localhost:8888', '90E79483998232C5529309656B0CFA5959A03AA94CCA4B396A2297144D3DDB95', '/county-dashboard', 200, '2024-06-13 19:38:27.137223', 0, 240271);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240275, 'countyadmin1', 'localhost:8888', '3E65FD8A81A01BA30A71D259490B63F12E07C4A35BBE0A7826128A28B1AA20F3', '/contest/county', 200, '2024-06-13 19:38:27.173829', 0, 240274);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240276, 'countyadmin1', 'localhost:8888', 'D7743B7C56304EBD3ECB2C2EAF79FDBB7FE32F1D81BB2C3D3AC8DBB418174F28', '/audit-board-asm-state', 200, '2024-06-13 19:38:32.175893', 0, 240275);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240277, 'countyadmin1', 'localhost:8888', '3607666A1CEB98901BB8BD6684ECDD7886FD6FD8CAB28C44AC749BBC6940666F', '/county-asm-state', 200, '2024-06-13 19:38:32.17588', 0, 240275);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240278, 'countyadmin1', 'localhost:8888', '97DB497738B51D47E73F903E583C1896591B9C4307E878E1D4D646BF1D78B5AE', '/county-dashboard', 200, '2024-06-13 19:38:32.191093', 0, 240275);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240279, 'countyadmin1', 'localhost:8888', 'A5B0A1780B6B679CD19A45A4605DABD51B13A6137F9F21798CFCAB2E2ED0DA0C', '/contest/county', 200, '2024-06-13 19:38:32.224354', 0, 240278);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240281, 'countyadmin1', 'localhost:8888', '7ED17A88F4834622B16F3E8192A50A58473658CB4A82F4AE0F91BA74DCACA66D', '/audit-board-asm-state', 200, '2024-06-13 19:38:37.227806', 0, 240279);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240280, 'countyadmin1', 'localhost:8888', 'F14CC8503BF2C05ACCF54AF87576ACEB22D5C40A1FE617A4BD4574884AAD7D7C', '/county-asm-state', 200, '2024-06-13 19:38:37.227929', 0, 240279);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240282, 'countyadmin1', 'localhost:8888', '8A794B55B608995BD8F21DE5F736F701588AB41E98CAF53CCF53F83002818598', '/county-dashboard', 200, '2024-06-13 19:38:37.241362', 0, 240279);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240283, 'countyadmin1', 'localhost:8888', 'DF07C4DF63A9A2ABAB89CE271DAB7FCCB5100C971490F4E42078B1DC9FC6DAFA', '/contest/county', 200, '2024-06-13 19:38:37.273291', 0, 240282);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240284, 'countyadmin1', 'localhost:8888', '9AB83D3A0E44C260B52D4E772D72DD59608C2CC6920A2D6BA1BAF707C1847614', '/audit-board-asm-state', 200, '2024-06-13 19:38:42.275754', 0, 240283);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240285, 'countyadmin1', 'localhost:8888', '6F0565FDDA9B1C3129691D06C927C3D297ABB748221EE2AD6BAC85D5FA11576F', '/county-asm-state', 200, '2024-06-13 19:38:42.277399', 0, 240283);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240286, 'countyadmin1', 'localhost:8888', 'EABB7F6FA650D710B38A3565616B2FE36F35DECA9D47016093884BCF776356B2', '/county-dashboard', 200, '2024-06-13 19:38:42.292629', 0, 240283);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240287, 'countyadmin1', 'localhost:8888', 'AA6CA5D0C57516364042FA86D96C6A170AF72646A0A6A59A50ABF13A50EC422D', '/contest/county', 200, '2024-06-13 19:38:42.326252', 0, 240286);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240288, 'countyadmin1', 'localhost:8888', '837F1A597BDE390C3B4614E8AD4B3095C615D8CB2BBA1E8DBC7A9AB52138595F', '/audit-board-asm-state', 200, '2024-06-13 19:38:47.327413', 0, 240287);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240289, 'countyadmin1', 'localhost:8888', 'EAE1CF724DC4E748A02C337259BF7261DF183724D2A7D014B01092CC2F6D3BB4', '/county-asm-state', 200, '2024-06-13 19:38:47.328206', 0, 240287);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240290, 'countyadmin1', 'localhost:8888', '547A44320DEB32C2E82AEA595FF85A653E3EF7671DE94F905859E55BB64471CD', '/county-dashboard', 200, '2024-06-13 19:38:47.340965', 0, 240287);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240291, 'countyadmin1', 'localhost:8888', 'D8646E42128F418FDECE271881022977CC9536C494667242852F0D4FA6AD7F0B', '/contest/county', 200, '2024-06-13 19:38:47.373805', 0, 240290);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240292, 'countyadmin1', 'localhost:8888', '721270216553843720B11763781B2278D9C69D98A5022ECEE10F52C6A62A6849', '/audit-board-asm-state', 200, '2024-06-13 19:38:52.378279', 0, 240291);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240293, 'countyadmin1', 'localhost:8888', '1FBEB014AFC6C9CC5C6D3FD050C2D375DDEB69A0B0958813478D5338D4B4DDFF', '/county-asm-state', 200, '2024-06-13 19:38:52.379779', 0, 240291);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240294, 'countyadmin1', 'localhost:8888', '4DDEFCEA1CC3B7983299451ED6E5B725CBD17B55D6E8C31D8CD8915035965876', '/county-dashboard', 200, '2024-06-13 19:38:52.394992', 0, 240291);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240295, 'countyadmin1', 'localhost:8888', 'FC28B92D926F1D0E4AC501D72DFA204151D41756BDABB5D99150DC3FC9AE864A', '/contest/county', 200, '2024-06-13 19:38:52.428815', 0, 240294);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240296, 'countyadmin1', 'localhost:8888', '379CFEDDF898C360340B2597D25099728EBB05D64FEAA2F849E87FE41619B1F6', '/county-asm-state', 200, '2024-06-13 19:38:57.431194', 0, 240295);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240297, 'countyadmin1', 'localhost:8888', '970F19BD54DF3428A3BFC5811D2D556373A201A7E3F97687919CA105291E2A6B', '/audit-board-asm-state', 200, '2024-06-13 19:38:57.431264', 0, 240295);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240298, 'countyadmin1', 'localhost:8888', '04D179675AE15EE129FAF33867ABE461C92D0F7DB705DEE910B07D1753CE27A6', '/county-dashboard', 200, '2024-06-13 19:38:57.449024', 0, 240295);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240299, 'countyadmin1', 'localhost:8888', 'AFC646C195405CE1423100C6F5BB7106374135CBA2F8DEEC06D28713BED07C8A', '/contest/county', 200, '2024-06-13 19:38:57.47865', 0, 240298);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240300, 'countyadmin1', 'localhost:8888', 'BF38929AC2BCE26B8C3C0280BF19781674620F73A806F2231ABF0AAEA222F848', '/audit-board-asm-state', 200, '2024-06-13 19:39:02.483118', 0, 240299);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240301, 'countyadmin1', 'localhost:8888', 'A0A63D457158F5372A88EA0CED541E70710E0DF06E1681AF3382B601C7A56A38', '/county-asm-state', 200, '2024-06-13 19:39:02.483569', 0, 240299);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240302, 'countyadmin1', 'localhost:8888', '0072FA417DB9A72CA3BB194E09FA05F072F950F115871DD89489C870C2D140B8', '/county-dashboard', 200, '2024-06-13 19:39:02.495957', 0, 240299);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240303, 'countyadmin1', 'localhost:8888', 'FC91A0EEF3CA1C79646E1907D2A9348907B13573B10DC3A799E5E28E581926DD', '/contest/county', 200, '2024-06-13 19:39:02.531476', 0, 240302);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240306, 'countyadmin1', 'localhost:8888', '0F83D6ECC9E09EEDC3FFD0EFF2B6BF9489C9FB021F3ADB4F6DCBAD914F3AC9D8', '/county-dashboard', 200, '2024-06-13 19:39:07.550524', 0, 240303);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240307, 'countyadmin1', 'localhost:8888', 'C3CBEF6A3EA08597E71CD93FE399FA276C2162BB3530B4BEEC320056825CA2B5', '/contest/county', 200, '2024-06-13 19:39:07.583871', 0, 240306);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240310, 'countyadmin1', 'localhost:8888', 'A8430B78426D8103FE2920EBA5A6A0B41821CB3CCD344932B1037D1FE5A230B7', '/county-dashboard', 200, '2024-06-13 19:39:12.601524', 0, 240307);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240311, 'countyadmin1', 'localhost:8888', '6F1B9ED1AF5D5715827110405FA5468FA7C626F2A50180F85E8117A8073E242A', '/contest/county', 200, '2024-06-13 19:39:12.657274', 0, 240310);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240314, 'countyadmin1', 'localhost:8888', '1B22E30A4740A782EEBDE9BBA8D3426F8BD16401A867D3E18C6EF8E86CC7CDFC', '/county-dashboard', 200, '2024-06-13 19:39:17.679665', 0, 240311);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240315, 'countyadmin1', 'localhost:8888', '7C8A6C45549D848BAB5ED36D418841DFFE0B931C56F91F0D7C5CB56A4F11FEDE', '/contest/county', 200, '2024-06-13 19:39:17.72937', 0, 240314);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240316, 'countyadmin1', 'localhost:8888', '913F94C0AE68C1B60AF27C9A94BD2FE073E8DF6E1E05C38B6C9497A1F799486A', '/audit-board-asm-state', 200, '2024-06-13 19:39:22.728283', 0, 240315);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240321, 'countyadmin1', 'localhost:8888', '0EFEFFFF1D743DD83622A5DB63C5AC98E76CA86120383B7DE0DC3397E08A6532', '/audit-board-asm-state', 200, '2024-06-13 19:39:27.77222', 0, 240319);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240325, 'countyadmin1', 'localhost:8888', 'FC25C63D31E14F17A59F251ECAD582FDAE77ED81CFB1620AF9613E409CCAD335', '/audit-board-asm-state', 200, '2024-06-13 19:39:32.821576', 0, 240323);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240328, 'countyadmin1', 'localhost:8888', '0964928D31A29397349D783D3A5C18C609388D70BC2FAF15BA754943E93C5D6F', '/audit-board-asm-state', 200, '2024-06-13 19:39:37.868843', 0, 240327);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240332, 'countyadmin1', 'localhost:8888', 'D3A437C35E73BE20BD4191A1CCCE2B534F43EB22ED20709966ACE7E08A89CB7E', '/county-asm-state', 200, '2024-06-13 19:39:42.915458', 0, 240331);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240304, 'countyadmin1', 'localhost:8888', '2AD5E094FBD626D40D718BFFC9B118623CEFB01B24815E60D43D7A9C50A09AA9', '/audit-board-asm-state', 200, '2024-06-13 19:39:07.535549', 0, 240303);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240309, 'countyadmin1', 'localhost:8888', '55822921DCE298EF058796275C4AE7C221F6964FE89624BC3B43CAC4C8025C78', '/audit-board-asm-state', 200, '2024-06-13 19:39:12.590737', 0, 240307);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240312, 'countyadmin1', 'localhost:8888', 'EBD4A065502CDFE0730FB80FCEACBDD3A25B9A2838501771CD723420687B4846', '/audit-board-asm-state', 200, '2024-06-13 19:39:17.66555', 0, 240311);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240318, 'countyadmin1', 'localhost:8888', '1AD8E227046BF0E2070B2238C79181AF70952C47671DF0D5DC1E084060D1EC0C', '/county-dashboard', 200, '2024-06-13 19:39:22.740731', 0, 240315);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240319, 'countyadmin1', 'localhost:8888', '7E2D1304C633AC65383515623AA0C0D406999E96399BEC927951C546860A3F63', '/contest/county', 200, '2024-06-13 19:39:22.769146', 0, 240318);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240322, 'countyadmin1', 'localhost:8888', 'B1A3CA4FBD3491C7969B4C513297BCA1B8A51005DE72B8884B030856120D48C9', '/county-dashboard', 200, '2024-06-13 19:39:27.786556', 0, 240319);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240323, 'countyadmin1', 'localhost:8888', 'BD55F9496A0446960866F822B8EE80D4BF66BAC0E64314A5A6CD4FE91FC84B2A', '/contest/county', 200, '2024-06-13 19:39:27.825445', 0, 240322);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240326, 'countyadmin1', 'localhost:8888', '4D817F1D62E4C9457C4F5C8A265995F3F3236A2CDE55652414B496E55ACF2A30', '/county-dashboard', 200, '2024-06-13 19:39:32.83931', 0, 240323);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240327, 'countyadmin1', 'localhost:8888', '7A567252953BA739CA346EBB2DFE18D2D1ACBAA7C7662CF59E6AAB408B557134', '/contest/county', 200, '2024-06-13 19:39:32.865807', 0, 240326);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240330, 'countyadmin1', 'localhost:8888', '7B3F8C3B6A4AC4F602CDD3325B2B2BD19F47C34B86F4029917C0AF43BE95B2A8', '/county-dashboard', 200, '2024-06-13 19:39:37.882222', 0, 240327);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240331, 'countyadmin1', 'localhost:8888', 'BF50E4C81EB25F117DE9C677E54A646118069E626FC2642EA3297F14DD050A54', '/contest/county', 200, '2024-06-13 19:39:37.915321', 0, 240330);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240334, 'countyadmin1', 'localhost:8888', 'EDB22826B6484BA8EEBCE83CF8F694BEC38242DAA3D7084CC887005BDDE69AFA', '/county-dashboard', 200, '2024-06-13 19:39:42.929488', 0, 240331);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240335, 'countyadmin1', 'localhost:8888', '17CE3D96127C7991B38087FEEAAA7A90A2D224FA8285000CA293A6695214BE87', '/contest/county', 200, '2024-06-13 19:39:42.957806', 0, 240334);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240305, 'countyadmin1', 'localhost:8888', 'F68B8648072D1FB9E0E5D28986415D40C9933C2F502601FC219E2DB2676AE28E', '/county-asm-state', 200, '2024-06-13 19:39:07.536341', 0, 240303);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240308, 'countyadmin1', 'localhost:8888', '7F84F77FCB6C713AAEE0486F208F4BC06A750772690DCD0E53A8585974071393', '/county-asm-state', 200, '2024-06-13 19:39:12.590541', 0, 240307);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240313, 'countyadmin1', 'localhost:8888', '3DCB7F49F8585EFA4796C4017429D587969FF0BD6A5ACCDD8980EA939A980142', '/county-asm-state', 200, '2024-06-13 19:39:17.666275', 0, 240311);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240317, 'countyadmin1', 'localhost:8888', 'BC0EF0FD32EA3871FBD906A1E40836F2FA59664362A37BB94D759C199C79447C', '/county-asm-state', 200, '2024-06-13 19:39:22.72844', 0, 240315);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240320, 'countyadmin1', 'localhost:8888', '40C425E4B5ACC0C5657055BB0F2A2FD2EEA2100B37062F6D57E7F3A17CC95FA7', '/county-asm-state', 200, '2024-06-13 19:39:27.772091', 0, 240319);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240324, 'countyadmin1', 'localhost:8888', '2A13FB0EA9C151ED6382451E5D5A90DCECA7E276F5490C3912379AC7A577AD5E', '/county-asm-state', 200, '2024-06-13 19:39:32.821823', 0, 240323);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240329, 'countyadmin1', 'localhost:8888', '675E74F1821D9F7D0C9B8CEFD709343312A7A22505FC1AD69E230C41707E9728', '/county-asm-state', 200, '2024-06-13 19:39:37.869307', 0, 240327);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240333, 'countyadmin1', 'localhost:8888', 'C15D2B9A97233409A80E644992C7112341FBD41A921CF38161C795AE08E8C933', '/audit-board-asm-state', 200, '2024-06-13 19:39:42.915228', 0, 240331);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240336, 'countyadmin1', 'localhost:8888', 'BB731AA4F7A4C6756675439767BA63580A3B6126C89C290E766EDA4B60FF98FF', '/county-asm-state', 200, '2024-06-13 19:39:47.95938', 0, 240335);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240337, 'countyadmin1', 'localhost:8888', 'F764199907D33497A385478CC8D23F52B06E025CD12905912E9948AB03BE57B4', '/audit-board-asm-state', 200, '2024-06-13 19:39:47.959185', 0, 240335);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240338, 'countyadmin1', 'localhost:8888', '47AA19946AF0527E6C9CD15719E6FFA0BF0319CD0F566E5E0E9D4E6E187B3CDC', '/county-dashboard', 200, '2024-06-13 19:39:47.971844', 0, 240335);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240339, 'countyadmin1', 'localhost:8888', '4C4EF24984C4D05F79CC62EE26CB325BDC9B631C55F92DFD6EBB7D8A86E4B9DF', '/contest/county', 200, '2024-06-13 19:39:48.008921', 0, 240338);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240340, 'countyadmin1', 'localhost:8888', '8607B19539CB9C34FE5542CE2FC4A4D914D3D25F44546AE5405A395D04563161', '/county-asm-state', 200, '2024-06-13 19:39:53.009616', 0, 240339);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240341, 'countyadmin1', 'localhost:8888', '0A7483552DD33ADDAEE2F3889642FF350F8BBE0CB968A2E508D4035AF3D519BF', '/audit-board-asm-state', 200, '2024-06-13 19:39:53.008959', 0, 240339);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240342, 'countyadmin1', 'localhost:8888', 'DE4FBDE5EDA752E2F9E2C171FE38FA573D37384A362D0072FF6F76C1B93BF5C7', '/county-dashboard', 200, '2024-06-13 19:39:53.020846', 0, 240339);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240343, 'countyadmin1', 'localhost:8888', '1E3BD9F730697E0DB6DE451001D0A06AF666516F3B20AE271D95CFE19C25A05D', '/contest/county', 200, '2024-06-13 19:39:53.054331', 0, 240342);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240344, 'countyadmin1', 'localhost:8888', '981F982E9E48CA660189B151032EE52FC4B40CA9983D7A5BD6BCFFA80ECE2FA6', '/county-asm-state', 200, '2024-06-13 19:39:58.083546', 0, 240343);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240345, 'countyadmin1', 'localhost:8888', 'A9A3E83B4CF4E0BBD267EF45DFD252A5554E92C8AF426EB2363EE33E69B792AA', '/audit-board-asm-state', 200, '2024-06-13 19:39:58.083404', 0, 240343);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240346, 'countyadmin1', 'localhost:8888', 'A4E2E44061B63FD90410A4022A60C4EED7374823CF7F1641D944B722B3BCA6A0', '/county-dashboard', 200, '2024-06-13 19:39:58.101921', 0, 240343);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240347, 'countyadmin1', 'localhost:8888', 'DF9F359D1F3FC2A9EBB7989D5ED685896A4C6D1D75444D9F6AE13C18EFA6C3ED', '/contest/county', 200, '2024-06-13 19:39:58.131105', 0, 240346);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240348, 'countyadmin1', 'localhost:8888', '7EFB505C9AEF15F89E45A1396A552FB2C928C7ACEFE3EFB0CEA8549DB537E048', '/audit-board-asm-state', 200, '2024-06-13 19:40:03.172785', 0, 240347);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240349, 'countyadmin1', 'localhost:8888', 'F534F4096C85E0D84EAEC96F2BCBA6128D6756D09AA3410420C52A0F068A16AC', '/county-asm-state', 200, '2024-06-13 19:40:03.173027', 0, 240347);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240350, 'countyadmin1', 'localhost:8888', 'BF53D148C1819E63B47D19DFF335B2E2F52F16D8578B395D707B70C5BF553D2D', '/county-dashboard', 200, '2024-06-13 19:40:03.191469', 0, 240347);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240351, 'countyadmin1', 'localhost:8888', 'C31F71ECE06CF7F97316CCC9C2B91EC3466EBABB90F8FBD2555851F6880E8912', '/contest/county', 200, '2024-06-13 19:40:03.224657', 0, 240350);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240352, 'countyadmin1', 'localhost:8888', 'AF4C31010D25624298A74EFF1ABF51D549E69F0BDF4237BE1450B46F1FA24CC3', '/audit-board-asm-state', 200, '2024-06-13 19:40:08.224674', 0, 240351);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240353, 'countyadmin1', 'localhost:8888', 'FAAE736BEF16FC1E7C6602A6BB511E05D825AE79A655E592DA7B5F2F78E0AA78', '/county-asm-state', 200, '2024-06-13 19:40:08.226417', 0, 240351);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240354, 'countyadmin1', 'localhost:8888', 'F83DCCDD57EFF82C53D6941101F60E63C5CBE5E049A029E0026A7D725178AD67', '/county-dashboard', 200, '2024-06-13 19:40:08.239202', 0, 240351);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240355, 'countyadmin1', 'localhost:8888', '6A128450D9E834705F13E411731EC4BD1C9731075170A214B9795137C38912CE', '/contest/county', 200, '2024-06-13 19:40:08.273977', 0, 240354);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240357, 'countyadmin1', 'localhost:8888', '15EEC9A40F65AA364ECAF24190350579FD682D4774E92DFAD71EA0D985AD0CAD', '/upload-file', 200, '2024-06-13 19:40:10.782949', 0, 240355);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240358, 'countyadmin1', 'localhost:8888', '38FDD264F8C0D448EA96F6746B0D6DBE1906C7993B24966A11D706124C0E6CAE', '/audit-board-asm-state', 200, '2024-06-13 19:40:10.829157', 0, 240357);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240359, 'countyadmin1', 'localhost:8888', '974B988E1231F9DA05188902F4FBC52422AFD79D7B4D299E4E0DA670BF0B01B9', '/county-asm-state', 200, '2024-06-13 19:40:10.829257', 0, 240357);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240360, 'countyadmin1', 'localhost:8888', '98504E4DD6F4BE914D3550DD6EC13480918158CD4A81D1BD8136DE06DA38733D', '/county-dashboard', 200, '2024-06-13 19:40:10.843126', 0, 240357);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240361, 'countyadmin1', 'localhost:8888', 'DBF3205AD1424A3870C5A1B3480759A6976E4A829463C966583CC6385726F2BB', '/contest/county', 200, '2024-06-13 19:40:10.888874', 0, 240360);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240362, 'countyadmin1', 'localhost:8888', 'FD5632381236F5D0009069157C80605BC23AC5EC5E9EBDAB9A5F609B54DFC408', '/import-cvr-export', 200, '2024-06-13 19:40:10.908699', 0, 240358);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240363, 'countyadmin1', 'localhost:8888', '082CBAFF00E053CFB534C2A229AA684BF63D00D50271AE83AEC56F8FA82540CD', '/audit-board-asm-state', 200, '2024-06-13 19:40:10.964731', 0, 240362);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240364, 'countyadmin1', 'localhost:8888', '0BD1C7E10D8ED7F776B46EBBCAF36F8A1E023919EB2664FC79A1E2601444A26F', '/county-asm-state', 200, '2024-06-13 19:40:10.965324', 0, 240362);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240365, 'countyadmin1', 'localhost:8888', '3D16844F0E449C0A0FD2B07666415E6DBC5E1B76AEA0A1AB4E8EA70B3F4AC87A', '/county-dashboard', 200, '2024-06-13 19:40:10.989859', 0, 240362);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240366, 'countyadmin1', 'localhost:8888', '408F31704CE4D3F6E860E8566040F4F71CFCB3CD7775B60A7F679858CA7FB712', '/contest/county', 200, '2024-06-13 19:40:11.010078', 0, 240365);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240367, 'countyadmin1', 'localhost:8888', '906C9E159A698F58E6A550F950AA2AF2584012457FB6C22771EEF2DFEA7318B5', '/audit-board-asm-state', 200, '2024-06-13 19:40:13.273364', 0, 240366);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240368, 'countyadmin1', 'localhost:8888', '3E6D4CA924CD945951F2101357321E723A63C0502E6BE36EB9675F635568B5E9', '/county-asm-state', 200, '2024-06-13 19:40:13.273435', 0, 240366);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240369, 'countyadmin1', 'localhost:8888', '43F96ADDF52878E2CFD4020FB48675E8F8C67B1F398C1F6599AFC20D470BA5AA', '/county-dashboard', 200, '2024-06-13 19:40:13.289791', 0, 240366);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240370, 'countyadmin1', 'localhost:8888', '2C5324063C07C027DD5BF263F4C4713C6AA3F92644EBCDAC6BB438F5D1335089', '/contest/county', 200, '2024-06-13 19:40:13.362635', 0, 240369);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240371, 'countyadmin1', 'localhost:8888', 'EEEF9083D6D0CB02232D03FB6A0CBC436C8054B309DFE4B5254E017B8F500433', '/audit-board-asm-state', 200, '2024-06-13 19:40:18.367028', 0, 240370);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240372, 'countyadmin1', 'localhost:8888', '13EC3A4C0EA1B11E5140F2577A4B1D0AD14BA7F391893BAC694BFEF3F30A1758', '/county-asm-state', 200, '2024-06-13 19:40:18.368041', 0, 240370);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240373, 'countyadmin1', 'localhost:8888', '1A02A63BFEF76544700041F87126CBCAADF3BFF4D475CBA0AEDDB02FDD882D60', '/county-dashboard', 200, '2024-06-13 19:40:18.382147', 0, 240370);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240374, 'countyadmin1', 'localhost:8888', '4D1E4D444200E436F75E009012B33BF471F90E7FF9906ED4272A1E5BF954B867', '/contest/county', 200, '2024-06-13 19:40:18.424595', 0, 240373);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240377, 'countyadmin1', 'localhost:8888', 'BBB3B351D8072F2B7673A29B42247791DDBFEFB5354D3432F587C3872BD4803A', '/county-dashboard', 200, '2024-06-13 19:40:23.446451', 0, 240374);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240378, 'countyadmin1', 'localhost:8888', 'B3EE199E44AE3F1C876F03CFC36EAF2C1F7658D81E1501A3788EB907CDB422D7', '/contest/county', 200, '2024-06-13 19:40:23.492729', 0, 240377);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240379, 'countyadmin1', 'localhost:8888', 'A40CDE8B991DB45A592B176FFF46D9FE3DE87B6D5F016DF3A6BED7CE11C0C783', '/delete-file', 200, '2024-06-13 19:40:24.865527', 0, 240378);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240382, 'countyadmin1', 'localhost:8888', '2EBA2DF19E86FF44821EFFA1A072B83AB69DBA27F33CBB5324B4126F127455DB', '/county-dashboard', 200, '2024-06-13 19:40:28.508775', 0, 240379);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240383, 'countyadmin1', 'localhost:8888', 'C1E3D304A913DFE31A09F117A50C6D1B2E23478E0CD667FF3A9C5CFD3EDD9297', '/contest/county', 200, '2024-06-13 19:40:28.543037', 0, 240382);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240386, 'countyadmin1', 'localhost:8888', 'A8B90F203AD63044629AFD6853EF2DC06D908962F73D9C5938B8474F900E3B42', '/county-dashboard', 200, '2024-06-13 19:40:33.560037', 0, 240383);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240387, 'countyadmin1', 'localhost:8888', '2EC722F0607058CF706449A93E8A9EA5A89DE81A96682B8FBDAFF9052D79B9EC', '/contest/county', 200, '2024-06-13 19:40:33.595317', 0, 240386);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240390, 'countyadmin1', 'localhost:8888', '1D0777B2F25E63CB0C29D600E109587000EA5DDF583F30D4290352582BDC4FA4', '/county-dashboard', 200, '2024-06-13 19:40:38.608642', 0, 240387);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240391, 'countyadmin1', 'localhost:8888', '98137811C3DF2F980D92B504DF734F90F322D91F100D1BD0B0FD880CDA9FD0FB', '/contest/county', 200, '2024-06-13 19:40:38.642136', 0, 240390);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240394, 'countyadmin1', 'localhost:8888', '429015B045C4D4CD873F24E0134E87A9BDE80129E00EB88FA804C1FA0DF950DF', '/county-dashboard', 200, '2024-06-13 19:40:43.6556', 0, 240391);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240395, 'countyadmin1', 'localhost:8888', '347144ADA9000B20E1054E5ED0CB791707089348DD6210E1EC8727F7D385815D', '/contest/county', 200, '2024-06-13 19:40:43.682762', 0, 240394);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240398, 'countyadmin1', 'localhost:8888', '4332C9C2962E7980959513D705BF397E86E05E9D44F7490E91E5D6BFC5494AA3', '/county-dashboard', 200, '2024-06-13 19:40:48.696438', 0, 240395);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240399, 'countyadmin1', 'localhost:8888', '9D5D425D2104DAA4E7EC3A2A05DD90218F2BD1EE5D52878C649A2E12C1890B11', '/contest/county', 200, '2024-06-13 19:40:48.724986', 0, 240398);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240402, 'countyadmin1', 'localhost:8888', '7D0C7322F4EE06419BFA4821CE7304F65F0D51DD3C6C90DC328BB6858CA14FDE', '/county-dashboard', 200, '2024-06-13 19:40:53.739527', 0, 240399);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240403, 'countyadmin1', 'localhost:8888', '581E204DE046A654DEADBD66CA2EBB3FC6BA14464F4F15C7AEE7FF35C4667479', '/contest/county', 200, '2024-06-13 19:40:53.769296', 0, 240402);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240404, 'countyadmin1', 'localhost:8888', 'DD117BFC2263A218A0B3A626C08274CD909234404F4241C2D135EEC076AAE2D4', '/audit-board-asm-state', 200, '2024-06-13 19:40:58.772944', 0, 240403);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240409, 'countyadmin1', 'localhost:8888', 'C88F40EDD9EFBE5D2AE7116FFC1904484910BB477F68001A4D4072CF740EC97A', '/county-asm-state', 200, '2024-06-13 19:41:03.821042', 0, 240407);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240412, 'countyadmin1', 'localhost:8888', '99D1251142EA1C37FB4F6B59A774C2061D88907494956B85B65B7FEA3D40F618', '/audit-board-asm-state', 200, '2024-06-13 19:41:08.8584', 0, 240411);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240417, 'countyadmin1', 'localhost:8888', '9060B1FBEB946ADA52E204E101A43CA75CA0EB9974E5DD252EF2F4DADC0CF18E', '/county-asm-state', 200, '2024-06-13 19:41:13.891202', 0, 240415);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240420, 'countyadmin1', 'localhost:8888', 'E3A1A8D2C14BACC8D7A5FBA5D03A8986C2A32DF7538E94ADD4FB63AD8A293696', '/audit-board-asm-state', 200, '2024-06-13 19:41:18.998293', 0, 240419);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240425, 'countyadmin1', 'localhost:8888', 'B3E3551136788ED938969C17F50A9BC98E72F4AECC8A37100C5DC749C6993262', '/county-asm-state', 200, '2024-06-13 19:41:24.05059', 0, 240423);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240429, 'countyadmin1', 'localhost:8888', 'EA22C6DED0E8FCD02EDE8D0678650063DC6F9C50C88C36B14A0A2F8A7C4FF87D', '/audit-board-asm-state', 200, '2024-06-13 19:41:29.113577', 0, 240427);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240433, 'countyadmin1', 'localhost:8888', '5D0FD6514D50520AB478E4B8A6C6E39A3C25F6D0C2A275466B6170506CE91471', '/county-asm-state', 200, '2024-06-13 19:41:34.165611', 0, 240431);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240436, 'countyadmin1', 'localhost:8888', '8AA1BE85F1DDFC0FDF180F1A41EE8DEA61950F21F7B3830C68BE1622F5A2ECAE', '/audit-board-asm-state', 200, '2024-06-13 19:41:39.215983', 0, 240435);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240375, 'countyadmin1', 'localhost:8888', '6BAAEFB6FE2792FEDBA610353DCF052CC484004C9A90DC27FC28711F3CA50CCF', '/audit-board-asm-state', 200, '2024-06-13 19:40:23.43157', 0, 240374);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240381, 'countyadmin1', 'localhost:8888', 'DD6DAA0770051110B5462E96C94B4187E6938502399BBC8204F5D5F3A7DC8A20', '/county-asm-state', 200, '2024-06-13 19:40:28.494101', 0, 240379);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240384, 'countyadmin1', 'localhost:8888', 'F6286022E0784CE8E272FC06D2144B966CF4D6D7855FDE8914446B77F15B4630', '/audit-board-asm-state', 200, '2024-06-13 19:40:33.547174', 0, 240383);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240389, 'countyadmin1', 'localhost:8888', 'D87AB6B93D04EF1AB994975A897B63ECBA0A58BCA7DDA68AC9CCD6BF294A2BBB', '/county-asm-state', 200, '2024-06-13 19:40:38.597485', 0, 240387);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240393, 'countyadmin1', 'localhost:8888', 'FCA064D6DB8526CA46753A038AA8F85410C13EB11522BDA8E35379F9DA338D48', '/county-asm-state', 200, '2024-06-13 19:40:43.643593', 0, 240391);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240397, 'countyadmin1', 'localhost:8888', '503A0D76FB6541B57363FF40E43AE38F969DCC0B490318F60A161EB8F3E5C1A1', '/county-asm-state', 200, '2024-06-13 19:40:48.684447', 0, 240395);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240400, 'countyadmin1', 'localhost:8888', '4B4B5D6760DE42F464597197569B34A3FE0B17D81AE08A50809466D5ABA3DD47', '/audit-board-asm-state', 200, '2024-06-13 19:40:53.727886', 0, 240399);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240405, 'countyadmin1', 'localhost:8888', '8CFCCD0A39A340B8BBB0F9165ADD70D7CCCAF542184C9FCF1980FB7EF0E61DFA', '/county-asm-state', 200, '2024-06-13 19:40:58.773094', 0, 240403);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240408, 'countyadmin1', 'localhost:8888', 'B1AB831E70787BE15FADF9CD630D5BDBFFFDCDBD4535FFEDA71B4D83E6C9AB13', '/audit-board-asm-state', 200, '2024-06-13 19:41:03.819947', 0, 240407);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240413, 'countyadmin1', 'localhost:8888', 'DBB36B9886F09BB3854DCF2D267973B837815639EBD9B11BB2D6CDB3DCC4DC47', '/county-asm-state', 200, '2024-06-13 19:41:08.859427', 0, 240411);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240416, 'countyadmin1', 'localhost:8888', '6F75296AE55A916B6C0614FC3631B3CF1D87D079CFF796BB384E7F4611571369', '/audit-board-asm-state', 200, '2024-06-13 19:41:13.890766', 0, 240415);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240421, 'countyadmin1', 'localhost:8888', '9AC45CFBCDD8F1677B2CFF3C5EFBA1AE6063115A510F0677FEDC75C260FC2636', '/county-asm-state', 200, '2024-06-13 19:41:18.998578', 0, 240419);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240424, 'countyadmin1', 'localhost:8888', 'A51A6454B23B08D7C0AAFB9BE5906F5DF8D797C9E87EB5ECDB9F9152ED2B6592', '/audit-board-asm-state', 200, '2024-06-13 19:41:24.049358', 0, 240423);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240428, 'countyadmin1', 'localhost:8888', '1785373921A70C2D35AD264F47C9FBD9C62E0EA487973328EB9A1C48A37070CC', '/county-asm-state', 200, '2024-06-13 19:41:29.113577', 0, 240427);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240432, 'countyadmin1', 'localhost:8888', '491D544463D9BB6F9D711C3133EB74E7C69B111BCAAA68BBC35FCD483C5F9174', '/audit-board-asm-state', 200, '2024-06-13 19:41:34.164393', 0, 240431);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240437, 'countyadmin1', 'localhost:8888', '36ABBD469444E42F50036E1A7650EF64F6C871EB47E483E800AE893247B8C813', '/county-asm-state', 200, '2024-06-13 19:41:39.216743', 0, 240435);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240376, 'countyadmin1', 'localhost:8888', '3BA1FB3B6E3C5F7C645DF621BD4EFFAE58C9A95A631085AC0AE74A26BCBBD575', '/county-asm-state', 200, '2024-06-13 19:40:23.432623', 0, 240374);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240380, 'countyadmin1', 'localhost:8888', 'B12FAEA372F54423C07991AA0C3A9C7205B05AD49D45B062C9847D85F4D8BA7D', '/audit-board-asm-state', 200, '2024-06-13 19:40:28.493282', 0, 240379);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240385, 'countyadmin1', 'localhost:8888', 'CD3D3FD916B0B6237FFD613A203F380AF7983CF2112E7908EA5A44C2825D1D35', '/county-asm-state', 200, '2024-06-13 19:40:33.54825', 0, 240383);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240388, 'countyadmin1', 'localhost:8888', '42678CFF3C7C2BFE7EE9F94ED3994337F20074451EF9DA8DC3155697366AB316', '/audit-board-asm-state', 200, '2024-06-13 19:40:38.597256', 0, 240387);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240392, 'countyadmin1', 'localhost:8888', '167013487F54599E36C44BE48FBB0FAAF31DC89A2338A03DB943F44B51B159DC', '/audit-board-asm-state', 200, '2024-06-13 19:40:43.643547', 0, 240391);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240396, 'countyadmin1', 'localhost:8888', '03873FC00B625369E63E889F084142AC8E3297039A16425989EA83176DD985AE', '/audit-board-asm-state', 200, '2024-06-13 19:40:48.68445', 0, 240395);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240401, 'countyadmin1', 'localhost:8888', 'D1804C2F8958A4DD8F935A8C27E042F6DBAA931A798B5CC75895706E5F3CFD48', '/county-asm-state', 200, '2024-06-13 19:40:53.727934', 0, 240399);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240406, 'countyadmin1', 'localhost:8888', '027281EA4D8FF20693CA954E40B210EE0972258F9FC8A0F6E58686DEFA73ADA0', '/county-dashboard', 200, '2024-06-13 19:40:58.788715', 0, 240403);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240407, 'countyadmin1', 'localhost:8888', '11AC92EC9A7DDD52E2505449E759655328B83FDDD4C2116561E40430E194665F', '/contest/county', 200, '2024-06-13 19:40:58.817649', 0, 240406);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240410, 'countyadmin1', 'localhost:8888', 'DF5774DF9A049A54EE2CC0A3BABB4B982A6422E6508E954ECD36FAF023BB6433', '/county-dashboard', 200, '2024-06-13 19:41:03.833266', 0, 240407);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240411, 'countyadmin1', 'localhost:8888', '5E7641D653EBE5836312FD223482598EE7CC5ED26FC7E0625464BE066666E1E5', '/contest/county', 200, '2024-06-13 19:41:03.859493', 0, 240410);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240414, 'countyadmin1', 'localhost:8888', '5324B470172FC6B9CC3C0BC4AF32AEF3E96D09142557FE063DD90D857FDBFB8C', '/county-dashboard', 200, '2024-06-13 19:41:08.865388', 0, 240411);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240415, 'countyadmin1', 'localhost:8888', '4B589B9A6DED5379D233D4AFC903A0DD9B6AD56243D1697BE98C66E3FDAA89D9', '/contest/county', 200, '2024-06-13 19:41:08.887544', 0, 240414);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240418, 'countyadmin1', 'localhost:8888', '41D29B28EFC31A45D7306C6966377A2D5371066935700EC5CE804AA1B0DF990F', '/county-dashboard', 200, '2024-06-13 19:41:13.90227', 0, 240415);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240419, 'countyadmin1', 'localhost:8888', '510EE67F10E09A81F8326083CBF069FC81FF068C432B3A047CF8CF56C66AE39B', '/contest/county', 200, '2024-06-13 19:41:13.932165', 0, 240418);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240422, 'countyadmin1', 'localhost:8888', '284760352D9799EE85A5AC6A212F7B6E24651F00649F3B4F643D53772F8B1CF1', '/county-dashboard', 200, '2024-06-13 19:41:19.009748', 0, 240419);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240423, 'countyadmin1', 'localhost:8888', '621E541B70436D43731B7438346303E1B15EAAA226A63B07B55A9887AAD8ACCD', '/contest/county', 200, '2024-06-13 19:41:19.041763', 0, 240422);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240426, 'countyadmin1', 'localhost:8888', '9D9D82A86D93DA2DD17BFE12825DCF3AB96004C96DA2FA4C5010F723E4E12A11', '/county-dashboard', 200, '2024-06-13 19:41:24.06263', 0, 240423);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240427, 'countyadmin1', 'localhost:8888', 'C084B7FE328545097B789084BFB96E0E42DF8EF8AF570D833D0B0FAFA0E1DF5A', '/contest/county', 200, '2024-06-13 19:41:24.110113', 0, 240426);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240430, 'countyadmin1', 'localhost:8888', '1754641236F69AB5ED7D3C9EF01F3F6175920A3E42B63FB6722FF51C7F7590E2', '/county-dashboard', 200, '2024-06-13 19:41:29.12537', 0, 240427);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240431, 'countyadmin1', 'localhost:8888', '06C9ACFEF4E459EA40721EA8F30ED94C4387604CA6E1706060322C8BE18A805D', '/contest/county', 200, '2024-06-13 19:41:29.158946', 0, 240430);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240434, 'countyadmin1', 'localhost:8888', 'E6A2A7F65EBCE72395C48F424C8FACCAA757A918DB80CF919FF726551E78101D', '/county-dashboard', 200, '2024-06-13 19:41:34.174891', 0, 240431);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240435, 'countyadmin1', 'localhost:8888', 'C302FE03256AE64823AA95CDD3D6E2AF734FAF75A3FAE52049F660CA8B1C0A8D', '/contest/county', 200, '2024-06-13 19:41:34.216212', 0, 240434);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240438, 'countyadmin1', 'localhost:8888', '5D78C9256038FC1F49E4F36104225CDC5D771CF3DD509F6DD1C8DC062B004DD8', '/county-dashboard', 200, '2024-06-13 19:41:39.224214', 0, 240435);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240439, 'countyadmin1', 'localhost:8888', 'EA987F5AAE9684F5E4AA5E2D09BF044F5ED1F8C4ADC78294C4EF071FA57C750A', '/contest/county', 200, '2024-06-13 19:41:39.254108', 0, 240438);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240440, 'countyadmin1', 'localhost:8888', 'ABC31E9E39BD0E0DFD49E5FA827C905DEA2D4AB98D23614A7993F37FD40F9E0F', '/audit-board-asm-state', 200, '2024-06-13 19:41:44.258437', 0, 240439);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240441, 'countyadmin1', 'localhost:8888', '01BAA10E367C97D83B66A8630CA5F87A580465E57915A6566E7D317DA53FD6CD', '/county-asm-state', 200, '2024-06-13 19:41:44.259063', 0, 240439);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240442, 'countyadmin1', 'localhost:8888', '76FBB35F8345A0CFA349E3D1F175A02C10AA0F31809E44412AEA83EF16827C6D', '/county-dashboard', 200, '2024-06-13 19:41:44.267497', 0, 240439);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240443, 'countyadmin1', 'localhost:8888', 'D9704E6C3B2B61C7BB2ADA22C53E94917F0EFF6FF09B92B5176409C2048D3155', '/contest/county', 200, '2024-06-13 19:41:44.303552', 0, 240442);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240444, 'countyadmin1', 'localhost:8888', '2B727FE10E7A40D028A7BD0964A79E9D8BF2E78B77635CE747C4FDC78372E54C', '/county-asm-state', 200, '2024-06-13 19:41:49.304779', 0, 240443);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240445, 'countyadmin1', 'localhost:8888', 'F855D91F5350A1479B92B190CB4FFC1D8FFC7F951A3F81B18CFE27E94520E2E1', '/audit-board-asm-state', 200, '2024-06-13 19:41:49.304731', 0, 240443);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240446, 'countyadmin1', 'localhost:8888', 'E09B8F6F5AA046DB8B45A69BFEAEE47938FEC9F9D27F64F8ABF4F54D3275ED3F', '/county-dashboard', 200, '2024-06-13 19:41:49.3131', 0, 240443);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240447, 'countyadmin1', 'localhost:8888', '8FBC1B749678AE09E949B86C8B28C04357C3AAD344491F2E0FF2C9E2AEE7A004', '/contest/county', 200, '2024-06-13 19:41:49.34424', 0, 240446);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240448, 'countyadmin1', 'localhost:8888', '2E6AEB5CE2BF210120C97A65ADB3072D0AB1EDAA8B7C5C73142F2BBBD38BC1C7', '/audit-board-asm-state', 200, '2024-06-13 19:41:55.293425', 0, 240447);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240449, 'countyadmin1', 'localhost:8888', 'E33F97E8BBC6FE1982FD2F4F6377A678A2EC32DF783BCBB1508305DB64994073', '/county-asm-state', 200, '2024-06-13 19:41:55.293713', 0, 240447);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240450, 'countyadmin1', 'localhost:8888', '9AA3BFE00FC803FBA669409CD74E19F0DAE8F1719BAC4C8A96295738D3FC1AFA', '/county-dashboard', 200, '2024-06-13 19:41:55.301565', 0, 240447);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240451, 'countyadmin1', 'localhost:8888', '4FF8E66A1B9EAAE7FA13330B5349F35783901FDDBEA69AC095C26236196D054B', '/contest/county', 200, '2024-06-13 19:41:55.329105', 0, 240450);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240452, 'countyadmin1', 'localhost:8888', '9377E7D722AE8F8CF871F94871EF311FB9AA2E68A6F23592CF64D8C328691382', '/audit-board-asm-state', 200, '2024-06-13 19:42:01.307507', 0, 240451);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240453, 'countyadmin1', 'localhost:8888', 'E9B7D88A3A8DF1D950CBF967710515C430EF01E25A713471228596615F071566', '/county-asm-state', 200, '2024-06-13 19:42:01.308086', 0, 240451);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240454, 'countyadmin1', 'localhost:8888', 'FB344B46AAF4F6628132D489B79E678F4BB9E82C53B39511C805DC62B15B6EFF', '/county-dashboard', 200, '2024-06-13 19:42:01.317897', 0, 240451);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240455, 'countyadmin1', 'localhost:8888', '880C85E84C4B968473C20B9091DF058AFBDC9433B59FA18209E97C0A6E7EFF8A', '/contest/county', 200, '2024-06-13 19:42:01.346832', 0, 240454);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240456, 'countyadmin1', 'localhost:8888', 'FBE1E2FC80D6F104C8960472DA20A3027F400C1172684C43574CBE4709DBC073', '/audit-board-asm-state', 200, '2024-06-13 19:42:07.315339', 0, 240455);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240457, 'countyadmin1', 'localhost:8888', 'C7222C24AA7154BFCC60BFD783F07FA5EA99185043455BFD01372FF2A1CDEBBC', '/county-asm-state', 200, '2024-06-13 19:42:07.315347', 0, 240455);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240458, 'countyadmin1', 'localhost:8888', '0D46B7BD829818092746EE5B24699D8E273455F71EDFC92927E6C4C0C717AAB1', '/county-dashboard', 200, '2024-06-13 19:42:07.326067', 0, 240455);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240459, 'countyadmin1', 'localhost:8888', '70064A0ABC117A97C7606D4D313670EEB9ED4167A0CCBB98149394CBD9FF3914', '/contest/county', 200, '2024-06-13 19:42:07.352597', 0, 240458);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240460, 'countyadmin1', 'localhost:8888', '291D54F3ECBCBDE53D72691CDA8C2AC9C198481EACD26DCBCC2C59083868F021', '/audit-board-asm-state', 200, '2024-06-13 19:42:13.342913', 0, 240459);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240465, 'countyadmin1', 'localhost:8888', '4A99BEEC3A739C39D3599ECFBA862EAF940523272E42C53CB41F0FBE74F02C9F', '/county-asm-state', 200, '2024-06-13 19:42:18.382721', 0, 240463);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240468, 'countyadmin1', 'localhost:8888', '2C3AB1564E1092A436AF3C53F126AE7B55886C1BB73363A645C98EB0D0463953', '/audit-board-asm-state', 200, '2024-06-13 19:42:23.43243', 0, 240467);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240473, 'countyadmin1', 'localhost:8888', '755B6F980772452BF4B5CE48B4777D2ED9D12C876567690900263E0C72E6F86C', '/county-asm-state', 200, '2024-06-13 19:42:28.49697', 0, 240471);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240476, 'countyadmin1', 'localhost:8888', '6BCCDDA914743FC799A90170AA57E021151D0C656CC6712E9C4C1324C8392F96', '/audit-board-asm-state', 200, '2024-06-13 19:42:33.537745', 0, 240475);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240481, 'countyadmin1', 'localhost:8888', '0E675D5E8F0FB4E46FA8F7DD2E476337B0378785ADFC9D5C2109DE42FA2A79FF', '/audit-board-asm-state', 200, '2024-06-13 19:42:39.288447', 0, 240479);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240484, 'countyadmin1', 'localhost:8888', '6D3E8BF7AE3709446CA652BEC7C6900DA70178FB305B9B8680FD4DD4533CD976', '/audit-board-asm-state', 200, '2024-06-13 19:42:44.336731', 0, 240483);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240491, 'countyadmin1', 'localhost:8888', '0B799E70FE2E8DAB84BF4129AB91B3CE3398C31BBE92FC33501F4BC5447EED7E', '/county-asm-state', 200, '2024-06-13 19:42:48.956435', 0, 240489);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240495, 'countyadmin1', 'localhost:8888', '49B679B8F9C5B6B7900483DF0F7787D5C8B93B13480155582E13C77BF54162E5', '/county-asm-state', 200, '2024-06-13 19:42:49.020511', 0, 240494);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240500, 'countyadmin1', 'localhost:8888', '76F7C49EC6FDA974ECF263E77BDAE5087690AACA89D3376AD0D075A9BF6AD95A', '/county-asm-state', 200, '2024-06-13 19:42:49.398257', 0, 240498);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240521, 'countyadmin1', 'localhost:8888', '654A5EB89FBCE3947E20371A1838015DB77771FAC0E5FB4F9A9F842BB1BCBC5F', '/county-dashboard', 200, '2024-06-13 19:42:54.466834', 0, 240502);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240522, 'countyadmin1', 'localhost:8888', 'C7031DD174D3737F597F182100C5BD9162509B53CD036C8E0AD3F09808822D79', '/contest/county', 200, '2024-06-13 19:42:54.540455', 0, 240521);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240525, 'countyadmin1', 'localhost:8888', '24A1562EB3B2285472232D334439D6203F879B23BFBD16F7A8453AF01897C4AB', '/county-dashboard', 200, '2024-06-13 19:42:59.551261', 0, 240522);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240526, 'countyadmin1', 'localhost:8888', '149590DDDFE8D001FA01CFBC58D1CF87859F47938D0F9450C83E6A084999F154', '/contest/county', 200, '2024-06-13 19:42:59.581298', 0, 240525);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240529, 'countyadmin1', 'localhost:8888', '2F182F0EEB2A3B5513A84BF13129404B63E41FE1E37EA0F29E5E1577F6AA164B', '/county-dashboard', 200, '2024-06-13 19:43:04.591263', 0, 240526);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240530, 'countyadmin1', 'localhost:8888', '948DDC3C195E40C3C16F5C53001A65A316D4EF89F78B9FAB51CC120ECAF5B5D2', '/contest/county', 200, '2024-06-13 19:43:04.623556', 0, 240529);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240533, 'countyadmin1', 'localhost:8888', '9C4B4939CACA28B446DCF64705000E514B4F83E4246F2C574C12562361E9769D', '/county-dashboard', 200, '2024-06-13 19:43:09.638882', 0, 240530);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240534, 'countyadmin1', 'localhost:8888', 'CECD138BE7857E2DB40FF06E7C433D192F8052616D958FC865EF592F23DB43E8', '/contest/county', 200, '2024-06-13 19:43:09.800926', 0, 240533);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240537, 'countyadmin1', 'localhost:8888', 'A169D6D99179325C743BD4192578AB3183BF4580D3C3C22E80B0E9525A79B586', '/county-dashboard', 200, '2024-06-13 19:43:14.694519', 0, 240534);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240538, 'countyadmin1', 'localhost:8888', '2030D4B60BAD4E79DC228EEB9410276FBB028F856D1A2247CB23DD1F67E62F3F', '/contest/county', 200, '2024-06-13 19:43:14.722456', 0, 240537);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240461, 'countyadmin1', 'localhost:8888', '1F6284B871A5C0E9F7E623389AF8F42BB898C90DA5B1B875CF8E15FD4D7CA5B2', '/county-asm-state', 200, '2024-06-13 19:42:13.343737', 0, 240459);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240464, 'countyadmin1', 'localhost:8888', 'FFE1F77C561A4A771B0BCDE8D5A177C425D5E9E34BDE8C2697385D608526E95F', '/audit-board-asm-state', 200, '2024-06-13 19:42:18.381358', 0, 240463);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240469, 'countyadmin1', 'localhost:8888', '4FD684B7746C29578A7BD04A6E5F1C01EBF132E9B757ACD4397527EAB862F6E2', '/county-asm-state', 200, '2024-06-13 19:42:23.433762', 0, 240467);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240472, 'countyadmin1', 'localhost:8888', '35DCFD07B7276D73DB9F2966284B8DE2A0B9CF03339C581F11EC28AF9519DAB6', '/audit-board-asm-state', 200, '2024-06-13 19:42:28.495982', 0, 240471);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240477, 'countyadmin1', 'localhost:8888', '695D21A3148134173E639FBCB4B03A4BEDB7F2F2E877B0F0033D0A361B8CCECE', '/county-asm-state', 200, '2024-06-13 19:42:33.538107', 0, 240475);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240480, 'countyadmin1', 'localhost:8888', 'F497A0103E6B03BD87F6C39DCEA92ED78B44E77810872487CF1BC9D8414AD15D', '/county-asm-state', 200, '2024-06-13 19:42:39.287764', 0, 240479);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240485, 'countyadmin1', 'localhost:8888', '069146ADED2066DD9A933FBB6A144231BD9A5823140B735A0D10B2225B40276F', '/county-asm-state', 200, '2024-06-13 19:42:44.337239', 0, 240483);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240490, 'countyadmin1', 'localhost:8888', '76BDFEA8A0D6C129C98CC5886AF733DA4BF5D9ADF629A215030F8F3F900DF8CE', '/audit-board-asm-state', 200, '2024-06-13 19:42:48.953986', 0, 240489);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240462, 'countyadmin1', 'localhost:8888', 'B7FC54E7BB7C33CE668C6428050EF51FED3333715E82F3416AAB90CC75E26C0B', '/county-dashboard', 200, '2024-06-13 19:42:13.352239', 0, 240459);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240463, 'countyadmin1', 'localhost:8888', 'F1332CB1D61991B20A2988BF5C53A820674B6FF774C023B0F1963B79B4AB8898', '/contest/county', 200, '2024-06-13 19:42:13.37791', 0, 240462);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240466, 'countyadmin1', 'localhost:8888', 'ED96D281BE1B58CB60CE7417EDE23FC1287AC599B71EFEE790F420C8FEAA8181', '/county-dashboard', 200, '2024-06-13 19:42:18.392512', 0, 240463);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240467, 'countyadmin1', 'localhost:8888', '7CD48A5CD2053FADAE5826022C34A1C7EB69A6D8F22B7B7FF70C586B0B8B119F', '/contest/county', 200, '2024-06-13 19:42:18.425503', 0, 240466);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240470, 'countyadmin1', 'localhost:8888', 'D5E3D32BB7AF02D0E21F9E0B9468C164ABCCA7FEF95A26567EF343B5E39F5685', '/county-dashboard', 200, '2024-06-13 19:42:23.43924', 0, 240467);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240471, 'countyadmin1', 'localhost:8888', 'EFC21B63012B7BDCDC1854CD9127346F90D613BA27BF88DBE9315CA5224D8896', '/contest/county', 200, '2024-06-13 19:42:23.491756', 0, 240470);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240474, 'countyadmin1', 'localhost:8888', '9A93ACD8890A3198D147EA492776923973EF68F924DA329D4BFA23C526B72C0C', '/county-dashboard', 200, '2024-06-13 19:42:28.503997', 0, 240471);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240475, 'countyadmin1', 'localhost:8888', '7B2B97B0ED05E8CC9E6F09A56CCE8BCD267DA0B0AEF76E4E209A5CD185443D6E', '/contest/county', 200, '2024-06-13 19:42:28.535829', 0, 240474);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240478, 'countyadmin1', 'localhost:8888', '8E2925D817CC4192ACB5BE11C5B835C06C5FB94CA232806A70304833980137E9', '/county-dashboard', 200, '2024-06-13 19:42:33.547275', 0, 240475);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240479, 'countyadmin1', 'localhost:8888', '4DE90B31C0BCA49F1FA23189EFC1E28B4C776F2A0D0476CD09DE46602220EF74', '/contest/county', 200, '2024-06-13 19:42:33.577966', 0, 240478);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240482, 'countyadmin1', 'localhost:8888', '8520B992D07BC2D632CD25A7D2B7144444D136B147D62D834E51F782E3451A54', '/county-dashboard', 200, '2024-06-13 19:42:39.29995', 0, 240479);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240483, 'countyadmin1', 'localhost:8888', '415B236AC318272706E68B1B4555525088DB8A68F55C3416036DF6067A121F66', '/contest/county', 200, '2024-06-13 19:42:39.335983', 0, 240482);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240486, 'countyadmin1', 'localhost:8888', 'C6763078782EB3A06A8554DEB22C4E86AED66C530E41E6DC511F1648E7BA53B9', '/county-dashboard', 200, '2024-06-13 19:42:44.347441', 0, 240483);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240487, 'countyadmin1', 'localhost:8888', '388BB496A8A5FEEAA98A5820FAF97281B3D3EA1321A9DB90329FF3ED264DD1D2', '/contest/county', 200, '2024-06-13 19:42:44.377504', 0, 240486);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240489, 'countyadmin1', 'localhost:8888', 'C2483938EF0BA0A641C724E5B56C9B14EAB8EC1DC236AB76B89B0B0AA74CBF14', '/upload-file', 200, '2024-06-13 19:42:48.916123', 0, 240487);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240492, 'countyadmin1', 'localhost:8888', '10C319D678B75B2422D5DDB3A2C4E9D948511850ED77F0A50D1A43880931C6C6', '/county-dashboard', 200, '2024-06-13 19:42:48.967424', 0, 240489);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240493, 'countyadmin1', 'localhost:8888', 'A7DBF0C2ED3852BD8F900EE4093BFDE48D1AA6A50F104E6A0614C1E2F285B355', '/import-cvr-export', 200, '2024-06-13 19:42:48.972211', 0, 240490);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240494, 'countyadmin1', 'localhost:8888', '7D23D4B480B75FA268BD748B68025D54C330B848BF50E06DEECFC34F693DE53F', '/contest/county', 200, '2024-06-13 19:42:49.010054', 0, 240493);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240496, 'countyadmin1', 'localhost:8888', '40D06ABA20E8B7F1ACD0827442C63D1EFB95F9E863C3DAF083107308F4C34E64', '/audit-board-asm-state', 200, '2024-06-13 19:42:49.020175', 0, 240494);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240497, 'countyadmin1', 'localhost:8888', '687AFE577F51046F3B40B2DA1D7EC222F13DA56B5E5A476B530C5E6DA30F4CCF', '/county-dashboard', 200, '2024-06-13 19:42:49.036457', 0, 240494);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240498, 'countyadmin1', 'localhost:8888', 'E990C06977B253E366174F9155848E1831A2CD60E04C8590432BF47E664DFFA3', '/contest/county', 200, '2024-06-13 19:42:49.062797', 0, 240497);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240499, 'countyadmin1', 'localhost:8888', '0ACDF14A5072A5FB73F0B1ADB084AE741CA3E4884D472DF5C62BF14396DC57AB', '/audit-board-asm-state', 200, '2024-06-13 19:42:49.398257', 0, 240498);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240501, 'countyadmin1', 'localhost:8888', '16058D84058709363C0F8537607BE593F6D4859FCCE00E3780F4DE3A2D2D7094', '/county-dashboard', 200, '2024-06-13 19:42:49.412214', 0, 240498);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240502, 'countyadmin1', 'localhost:8888', '66A8412444AD47DAC2BCE4A909931EA07DE89154F36B6E9E6F315BED7506C58F', '/contest/county', 200, '2024-06-13 19:42:49.45074', 0, 240501);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240519, 'countyadmin1', 'localhost:8888', 'D50E1E506CC3099F9B4AFC5DFBB1A60136E81D12ED0F9B1E445ECB1EEF50C9D2', '/audit-board-asm-state', 200, '2024-06-13 19:42:54.451369', 0, 240502);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240520, 'countyadmin1', 'localhost:8888', 'F492BF3CC97411BFC6142653270FB6447F0B13C24F61AD04817970DBA0D87973', '/county-asm-state', 200, '2024-06-13 19:42:54.452737', 0, 240502);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240523, 'countyadmin1', 'localhost:8888', '092B44377488CE90FFB5296DDFF7A90555CC66EA2D8B290624A2D8461CDBAF1C', '/audit-board-asm-state', 200, '2024-06-13 19:42:59.540348', 0, 240522);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240524, 'countyadmin1', 'localhost:8888', 'AA8DE2BDF5378B48F37EF54ACE23680D60C82BF332EAB639C7F9FFFCB8C167F0', '/county-asm-state', 200, '2024-06-13 19:42:59.540657', 0, 240522);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240528, 'countyadmin1', 'localhost:8888', 'E866D0960C84185EE4606586C4FD42316C39499D0C823663C5F18B3A13454385', '/county-asm-state', 200, '2024-06-13 19:43:04.58184', 0, 240526);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240527, 'countyadmin1', 'localhost:8888', 'ABAE39CB8926D628BFDA2B4D055E26D92D0857A29EE77DAF74CF8BA5C5174641', '/audit-board-asm-state', 200, '2024-06-13 19:43:04.580935', 0, 240526);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240531, 'countyadmin1', 'localhost:8888', '22E720354CA174B1B55C1102875250CF20E1D22BEDE4A52DB1DAD19FEA318FDD', '/county-asm-state', 200, '2024-06-13 19:43:09.628839', 0, 240530);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240532, 'countyadmin1', 'localhost:8888', '41603BFD7194BEBE01D249D89A2F4EE09C147BBB87053BDDDC3CF67BD002E117', '/audit-board-asm-state', 200, '2024-06-13 19:43:09.629147', 0, 240530);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240535, 'countyadmin1', 'localhost:8888', '7026E03E1CD283CB9285AB8C45D6330257265EAD3D623A9F6952E0F214C6B78D', '/county-asm-state', 200, '2024-06-13 19:43:14.682951', 0, 240534);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240536, 'countyadmin1', 'localhost:8888', '7CB1EEA1E4E36415C8E25E5FD553D05D3E233E2C531D544816FB43A5F15F8AD4', '/audit-board-asm-state', 200, '2024-06-13 19:43:14.682951', 0, 240534);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240540, 'countyadmin1', 'localhost:8888', '72632BE5161203D9DF3AA77585FBAA61E03B4D54CEA34E51890CD234B7C1F6AA', '/audit-board-asm-state', 200, '2024-06-13 19:43:19.787244', 0, 240538);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240539, 'countyadmin1', 'localhost:8888', '96EF2F3BFDF4DF17A1C9FC07D5722E3ACCF9C856106F2D8465E1B01125EE5D90', '/county-asm-state', 200, '2024-06-13 19:43:19.787266', 0, 240538);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240541, 'countyadmin1', 'localhost:8888', 'FD96D8D4316793E7D4B6725B025B782019D6A52B8144C78C7C6DCDAEA333345D', '/county-dashboard', 200, '2024-06-13 19:43:19.805351', 0, 240538);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240542, 'countyadmin1', 'localhost:8888', 'A59A0729F1BC210C465960D87F33B9D1B92EDBFF0E79760D224A6161087C08C3', '/contest/county', 200, '2024-06-13 19:43:19.838949', 0, 240541);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240544, 'countyadmin1', 'localhost:8888', 'D4AAA1BCC403CDAA2BE916D5B7DC6C72C227C616D917120F10706FEBBFB04586', '/upload-file', 200, '2024-06-13 19:43:21.071395', 0, 240542);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240545, 'countyadmin1', 'localhost:8888', '6BFEEF2141DB1BFDCA510A95953F446267D0FF6C3C3CDF75080AA2BDD4FDD107', '/audit-board-asm-state', 200, '2024-06-13 19:43:21.10041', 0, 240544);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240546, 'countyadmin1', 'localhost:8888', '1F84515E8E2DBF6BA24E6872E27D0489CD006D5EB8DC1C2ACA371D5923F03D48', '/county-asm-state', 200, '2024-06-13 19:43:21.101708', 0, 240544);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240547, 'countyadmin1', 'localhost:8888', '6473755B13C96D2C18CB03FC9ECC32FFCCE76D4897D09F2F193CA119E5AEEB03', '/county-dashboard', 200, '2024-06-13 19:43:21.118158', 0, 240544);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240549, 'countyadmin1', 'localhost:8888', '6DB86BDC1FC7D383C9D57AFD83384A1872DF86DFECF662DAFE9DD74AC2A9A566', '/contest/county', 200, '2024-06-13 19:43:21.154428', 0, 240547);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240550, 'countyadmin1', 'localhost:8888', '635414EB7792C021EB6837AA57895A393EDEBEA7A01B00F7E44D88D6AF81E13C', '/import-ballot-manifest', 200, '2024-06-13 19:43:21.166098', 0, 240544);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240551, 'countyadmin1', 'localhost:8888', 'ACD470C801260D23BF6E6243D2ED0072967C3C52663EC73AC2F83DB4BEEA29EA', '/audit-board-asm-state', 200, '2024-06-13 19:43:21.200753', 0, 240550);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240552, 'countyadmin1', 'localhost:8888', '437D694532B844D49DF22D53D8F8A7D12AF3CF98FCE7180F8A2CB5A445204670', '/county-asm-state', 200, '2024-06-13 19:43:21.202689', 0, 240550);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240555, 'countyadmin1', 'localhost:8888', '885637D960F25E0F62C189300F9C15AE6B2F6CED6B4C8C132081B82D145F32A2', '/audit-board-asm-state', 200, '2024-06-13 19:43:24.839201', 0, 240554);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240560, 'countyadmin1', 'localhost:8888', '7CEB6067DCF5FAF7DCFF457F84F981EB69CA9DBB9AA562A92EA9BC7E3B964431', '/county-asm-state', 200, '2024-06-13 19:43:29.904637', 0, 240558);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240563, 'countyadmin1', 'localhost:8888', '1F570036FBABAB0913DA77885646248D99AED78609C74461AECD905B7967DDD1', '/audit-board-asm-state', 200, '2024-06-13 19:43:34.954698', 0, 240562);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240568, 'countyadmin1', 'localhost:8888', '4F3D0F4C56825FAA35B80DA75DAC9357C0D462145D8D4AB0ED6AE7C1A860DE71', '/audit-board-asm-state', 200, '2024-06-13 19:43:39.994621', 0, 240566);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240553, 'countyadmin1', 'localhost:8888', 'C753C8ADB9B5695922DF9E4E994811D14A4232091E888EDE82FA71A98FCE8FAF', '/county-dashboard', 200, '2024-06-13 19:43:21.234113', 0, 240550);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240554, 'countyadmin1', 'localhost:8888', '4A69020EACA6F8A97B97F2A59ACAAD60AB899DCD31011CCC21CB3E51A851F001', '/contest/county', 200, '2024-06-13 19:43:21.291735', 0, 240553);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240557, 'countyadmin1', 'localhost:8888', 'CACF9E411E0F4B7C248EC6B4DC44D439F03DC8D9A54D9648CD7EBB6506140242', '/county-dashboard', 200, '2024-06-13 19:43:24.858855', 0, 240554);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240558, 'countyadmin1', 'localhost:8888', 'CE208C102FA1BDD1F467D1677637347307892C120B4A280891F7D24CCF534416', '/contest/county', 200, '2024-06-13 19:43:24.893203', 0, 240557);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240561, 'countyadmin1', 'localhost:8888', 'B0007D1F48C6E2D2008139A7F02A12E1B753FBDAE615C49A771A78B6A013EBBD', '/county-dashboard', 200, '2024-06-13 19:43:29.923696', 0, 240558);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240562, 'countyadmin1', 'localhost:8888', '1CBA8986FD847615CCA7F5A910CD5B0EFDD7934DBB1400D2287B6CF18E6A4894', '/contest/county', 200, '2024-06-13 19:43:29.959699', 0, 240561);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240565, 'countyadmin1', 'localhost:8888', 'BE0E58C00E5899AFFAAB44D3AD882CEDD60C478FF083A17303DBB8D3AE62163F', '/county-dashboard', 200, '2024-06-13 19:43:34.967297', 0, 240562);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240566, 'countyadmin1', 'localhost:8888', '65FDC7D7070CB47045490AAED0F7231F9F2D9CB1E0135875E3E0A1A787D1B6A2', '/contest/county', 200, '2024-06-13 19:43:34.995226', 0, 240565);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240569, 'countyadmin1', 'localhost:8888', '8CA184346B8655554E6726DDFB3F6F71CB75E098EC97A516DDA219E45BE4D0DE', '/county-dashboard', 200, '2024-06-13 19:43:40.005833', 0, 240566);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240570, 'countyadmin1', 'localhost:8888', 'B56761AB9363807562B2D42EF3CAF26E90A81EA77250CB9E7DC6DBBF353C0AB3', '/contest/county', 200, '2024-06-13 19:43:40.037672', 0, 240569);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240571, '(unauthenticated)', 'localhost:8888', '10D266A309853ACE28B7C4204E752BE7D9FF79C101483AB28B902DC9EAC5B844', '/unauthenticate', 200, '2024-06-13 19:43:40.584565', 0, 240570);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240556, 'countyadmin1', 'localhost:8888', 'C1BAC8A2EADF11626234A5CFDC923C95D6AB5C5C86D795FB8DAF5CA5C214B0AA', '/county-asm-state', 200, '2024-06-13 19:43:24.84066', 0, 240554);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240559, 'countyadmin1', 'localhost:8888', '465B37FEC3C76A7412E3E412608423E761CDF20F4EB87453125C90E2FB6162A1', '/audit-board-asm-state', 200, '2024-06-13 19:43:29.903152', 0, 240558);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240564, 'countyadmin1', 'localhost:8888', '79FE6B2EB3BDACCD912B6994E3FB98002C040B9085C6367AD46F65821111311F', '/county-asm-state', 200, '2024-06-13 19:43:34.955509', 0, 240562);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240567, 'countyadmin1', 'localhost:8888', '00B3FD1D20627C8B1C0A1C636A78DC99F19DDE9FD2BEEF574712B3721A6C2DF3', '/county-asm-state', 200, '2024-06-13 19:43:39.993661', 0, 240566);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240572, 'countyadmin2', 'localhost:8888', '7321B3C05346A6C04D06D5CA796E1E7720CCF5B419C0ED525D79C332067E3066', '/auth-admin', 200, '2024-06-13 19:43:48.474096', 0, 240571);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240573, 'countyadmin2', 'localhost:8888', '7EFFE1F7A5F88EC4044FB0216E6D935F009F25AD0EAB47E788D7B4F50C0AF92C', '/auth-admin', 200, '2024-06-13 19:43:49.976052', 0, 240572);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240574, 'countyadmin2', 'localhost:8888', '58F52DA450EFFE700A41A2768A62F4006E6303C0B5126A7B4253B98F7DB202BE', '/audit-board-asm-state', 200, '2024-06-13 19:43:50.029229', 0, 240573);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240575, 'countyadmin2', 'localhost:8888', 'A76CD097B922FB5CB6B4058DA4CFC133F0B55C1B819F112127E2A2CDD6A5CF2D', '/county-asm-state', 200, '2024-06-13 19:43:50.030883', 0, 240573);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240576, 'countyadmin2', 'localhost:8888', 'F03E68A4442D759A06EC74F7046A3B7185EC9A057FE2836F11098B55F14305CF', '/county-dashboard', 200, '2024-06-13 19:43:50.04502', 0, 240573);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240577, 'countyadmin2', 'localhost:8888', 'DA3D9C92A2EA3B8B43BD274F51FEFD6FF96F2E0F64A6F35F43BC69EBA0481A7D', '/contest/county', 200, '2024-06-13 19:43:50.116426', 0, 240576);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240578, 'countyadmin2', 'localhost:8888', 'C91091767864FF93A8776B5489A4095C4B58913E9DEE6003DEC011F5C365C918', '/audit-board-asm-state', 200, '2024-06-13 19:43:55.126512', 0, 240577);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240579, 'countyadmin2', 'localhost:8888', 'EF182CA4480B02F4C58678CCBDF3D15D17E67BA2C0ABBC889A542F204FA0E057', '/county-asm-state', 200, '2024-06-13 19:43:55.127451', 0, 240577);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240580, 'countyadmin2', 'localhost:8888', '7AF006A447B9E541CA9CFE9C91CF22702F476155EB9ADA2BF4AF5ECA0F9BF9C6', '/county-dashboard', 200, '2024-06-13 19:43:55.13609', 0, 240577);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240581, 'countyadmin2', 'localhost:8888', 'AABF75DF923F93DB6733F1C1E2FF71F92F56E62C53FE4852927395A31E259C32', '/contest/county', 200, '2024-06-13 19:43:55.19098', 0, 240580);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240583, 'countyadmin2', 'localhost:8888', 'F4E5DC239B552EF3FAF097C11FE0DC3B56B8A7115CA40E6A9BD869238A10762A', '/audit-board-asm-state', 200, '2024-06-13 19:44:00.198805', 0, 240581);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240582, 'countyadmin2', 'localhost:8888', '6B24EF9ED888F2433E7960AF868C2EEF512CBCE665EE75DF6D5922FE173D5456', '/county-asm-state', 200, '2024-06-13 19:44:00.19871', 0, 240581);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240584, 'countyadmin2', 'localhost:8888', '8A01917705B9992FCA56B4CDEFCE31D7D0765910D04B4FF99272AEA7C17D1E43', '/county-dashboard', 200, '2024-06-13 19:44:00.206285', 0, 240581);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240585, 'countyadmin2', 'localhost:8888', 'BF4FAB4D182EB09347CF4841C245CB75E6F70BBF3EEB4A1FEADF712B54B63980', '/contest/county', 200, '2024-06-13 19:44:00.250493', 0, 240584);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240586, 'countyadmin2', 'localhost:8888', '0D979E1F2A1A545C1A812F1A4D3D2C3210400F6765F7CB3A9BC2F0C079DF6B1E', '/county-asm-state', 200, '2024-06-13 19:44:05.255601', 0, 240585);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240587, 'countyadmin2', 'localhost:8888', '646D4A3624280A235F2D1B1FB6133C439A7BEEB3A6AFB5044C3C503264148FF6', '/audit-board-asm-state', 200, '2024-06-13 19:44:05.255261', 0, 240585);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240588, 'countyadmin2', 'localhost:8888', '63A937207D336CBAE28023EE864ED1B74A76C594BC4D1AF5A7B40F3FE3303E7A', '/county-dashboard', 200, '2024-06-13 19:44:05.264392', 0, 240585);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240589, 'countyadmin2', 'localhost:8888', '527BE11A404B687A2E826A6F8CC934956260B5FB54BA75DBE4EDFD77315EA1C2', '/contest/county', 200, '2024-06-13 19:44:05.295121', 0, 240588);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240591, 'countyadmin2', 'localhost:8888', '1601C00FA467B4175C514DB78E615D496499DE99960092F15A3FB1B63328BD74', '/upload-file', 200, '2024-06-13 19:44:06.234336', 0, 240589);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240592, 'countyadmin2', 'localhost:8888', '49FC947BCFE5FD7EEC0EAC7BE63C398443AAE59BD24A6B3E38EAB8EED09160A9', '/audit-board-asm-state', 200, '2024-06-13 19:44:06.263653', 0, 240591);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240593, 'countyadmin2', 'localhost:8888', '683C6525C1E7622E767418C5ADFE931E5AFC94E3A4EAEA857AD89E463782B055', '/county-asm-state', 200, '2024-06-13 19:44:06.266518', 0, 240591);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240594, 'countyadmin2', 'localhost:8888', '86906DC7A5B95B55535BF8CABC9C90AE3ED5B74A0FFF12FDED74E5A04C29FFA9', '/county-dashboard', 200, '2024-06-13 19:44:06.276352', 0, 240591);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240596, 'countyadmin2', 'localhost:8888', 'D2800B230E299C3871BAA63CF6FD32E00ECED9A9EEAE32432FDF790261661520', '/import-ballot-manifest', 200, '2024-06-13 19:44:06.297404', 0, 240591);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240597, 'countyadmin2', 'localhost:8888', '01FF9CF6699782CB5178F6591572852B139F02D910EA8BF52A449A688B9980E2', '/contest/county', 200, '2024-06-13 19:44:06.315928', 0, 240594);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240598, 'countyadmin2', 'localhost:8888', '884F769F69343AB967D4A7D80285402DBA9DB87E7FECCE36BFB937AAE77306D8', '/audit-board-asm-state', 200, '2024-06-13 19:44:06.339916', 0, 240597);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240599, 'countyadmin2', 'localhost:8888', '51ABA7177824B0AFC20EA12A5CF539C82119050024175F00BCCA6564A188A4F3', '/county-asm-state', 200, '2024-06-13 19:44:06.340675', 0, 240597);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240600, 'countyadmin2', 'localhost:8888', '733A56006BD5A554602EC9E58AE5F8D5F8211FA9A877EC0693B76A2591EEB40B', '/county-dashboard', 200, '2024-06-13 19:44:06.349257', 0, 240597);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240601, 'countyadmin2', 'localhost:8888', '1296F46F181A64E6F4AFC03C895276366E53AA0DD361B24E5D6A5368DEED9A72', '/contest/county', 200, '2024-06-13 19:44:06.390857', 0, 240600);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240602, 'countyadmin2', 'localhost:8888', '4D1DB10B43349545E62D378C80E0A5F898D711F0CBA811DC9F05C87C32BE1F6D', '/audit-board-asm-state', 200, '2024-06-13 19:44:10.299796', 0, 240601);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240603, 'countyadmin2', 'localhost:8888', '05F8F8F9789F57526114929A2334EAD51949E2F5304AEE2B2DE82BC6D33CA436', '/county-asm-state', 200, '2024-06-13 19:44:10.300104', 0, 240601);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240604, 'countyadmin2', 'localhost:8888', 'EF6EFB67FA9A8459A0FA16CD665B81DDD7D038B9043B90E7F11C29DB14EF6037', '/county-dashboard', 200, '2024-06-13 19:44:10.307141', 0, 240601);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240605, 'countyadmin2', 'localhost:8888', 'E55FB260337E7DE3601AA8F6B6CA2FC97B13AC59F6CBE7651C4315F870BF61D5', '/contest/county', 200, '2024-06-13 19:44:10.335145', 0, 240604);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240606, 'countyadmin2', 'localhost:8888', '5DB872720D9A3FE9F5A24EF6B2CC319FB1864994A693BEE74203F229B5961DAD', '/audit-board-asm-state', 200, '2024-06-13 19:44:15.339942', 0, 240605);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240607, 'countyadmin2', 'localhost:8888', 'A6F5E98377437C131178D74554F7945121EB8556A6CBC19749332CA39489D54C', '/county-asm-state', 200, '2024-06-13 19:44:15.340081', 0, 240605);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240608, 'countyadmin2', 'localhost:8888', 'EC22AD739DEC89C48F09E88C74E3C9C2E7B38907FEB17BA8431E8A1C8F012AB0', '/county-dashboard', 200, '2024-06-13 19:44:15.349375', 0, 240605);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240609, 'countyadmin2', 'localhost:8888', '4942C9F6DF59A76D442B7243563FC30D689A5F1BA735D0D53430BFD24FA14C1F', '/contest/county', 200, '2024-06-13 19:44:15.397229', 0, 240608);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240610, 'countyadmin2', 'localhost:8888', '81D7D064E5E35C0F9A63B56E23A3364F3FEA4170C2AABFF08E2F7DB256F36BE0', '/audit-board-asm-state', 200, '2024-06-13 19:44:20.38691', 0, 240609);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240611, 'countyadmin2', 'localhost:8888', '4EFAB590422BF1DB97482E92540BF15EBC37E8F76C2B68EA10E0DF3B7DEC97BE', '/county-asm-state', 200, '2024-06-13 19:44:20.38697', 0, 240609);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240612, 'countyadmin2', 'localhost:8888', '3BAFAB25226AD3BF45DE9AF67EA8D5ED82C7E102B7E212E83D3DFCE8C47D27E4', '/county-dashboard', 200, '2024-06-13 19:44:20.394153', 0, 240609);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240613, 'countyadmin2', 'localhost:8888', '8578E4FC5BC0DDA79A9DCCDAAFEFF9BC5168F501C5E6744BF1DCCE472B8902F1', '/contest/county', 200, '2024-06-13 19:44:20.42977', 0, 240612);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240614, 'countyadmin2', 'localhost:8888', '4F1B7F85086F0EDEDD9AA761A27783A27084EF4E811B0BC905CA62373CE71E83', '/audit-board-asm-state', 200, '2024-06-13 19:44:25.429905', 0, 240613);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240615, 'countyadmin2', 'localhost:8888', '4508597BE4FD2200F7163B482B5A575E4026730D572768BA979192380107DB54', '/county-asm-state', 200, '2024-06-13 19:44:25.431408', 0, 240613);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240620, 'countyadmin2', 'localhost:8888', '6BE6E8FD171E397DDE5C42FB9C82E3AF76598CE47CF8A01127C09D41979C3656', '/audit-board-asm-state', 200, '2024-06-13 19:44:27.129457', 0, 240619);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240616, 'countyadmin2', 'localhost:8888', '8E7A5C26F0433CA1B350C203D4B8D7D633586E53832F3D6DC598833C00A758FD', '/county-dashboard', 200, '2024-06-13 19:44:25.439076', 0, 240613);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240617, 'countyadmin2', 'localhost:8888', '6B2BF0940031D626CD08EE8304C0BA9AB63CBAA2F7574570AB09AAC438B4412D', '/contest/county', 200, '2024-06-13 19:44:25.468915', 0, 240616);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240619, 'countyadmin2', 'localhost:8888', 'C529E03A1BABEC8E3D870DAB932FAE063625A3F2234D3D5056C322C9358BC7D8', '/upload-file', 200, '2024-06-13 19:44:27.091996', 0, 240617);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240622, 'countyadmin2', 'localhost:8888', '1ECAE3552A0F82616EECDC5002E0FC5F8A881420C3208F48121829F1CBF66475', '/county-dashboard', 200, '2024-06-13 19:44:27.142084', 0, 240619);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240627, 'countyadmin2', 'localhost:8888', 'D6251179BB429B3B8E889172226509C2B6ABFE83BCFF8F647C30353745275936', '/county-dashboard', 200, '2024-06-13 19:44:27.195439', 0, 240623);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240628, 'countyadmin2', 'localhost:8888', '085B1A5DF09549438C8A05638E321431F4402AAC4BEC3E2C105A52D0E71CAF31', '/contest/county', 200, '2024-06-13 19:44:27.215641', 0, 240627);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240641, 'countyadmin2', 'localhost:8888', '8B31AC0A8C1152F8D3CA31C692358B4250047842E5EE776F021A044D6CB104FE', '/audit-board-asm-state', 200, '2024-06-13 19:44:30.474115', 0, 240628);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240645, 'countyadmin2', 'localhost:8888', '88CE0E0A77A01A354F5BA6F907EEF1966AD24AFD1264BC188ECFE4F5E5A774D5', '/audit-board-asm-state', 200, '2024-06-13 19:44:35.538851', 0, 240644);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240653, 'countyadmin3', 'localhost:8888', '9C6BDEA0248424F48B7B4D83CF2539803D617AD2D25789B0671B63C1E384E111', '/county-asm-state', 200, '2024-06-13 19:44:44.36546', 0, 240651);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240656, 'countyadmin3', 'localhost:8888', 'DB91ECBBA840A9E33989310F0FF5B8329650855A8DC2F4B70F0B61F88CF585AB', '/audit-board-asm-state', 200, '2024-06-13 19:44:49.458417', 0, 240655);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240661, 'countyadmin3', 'localhost:8888', 'A2ABAD25EEEA7B496517E390702CAE5E3ED0E3579CB9D3CD215FEE8EA466E064', '/county-asm-state', 200, '2024-06-13 19:44:54.546994', 0, 240659);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240664, 'countyadmin3', 'localhost:8888', '4F1161DCF307F7B87978968EC4F52AA61EB7C9B4B848BE5567ED23E020F6B30F', '/audit-board-asm-state', 200, '2024-06-13 19:44:59.605822', 0, 240663);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240669, 'countyadmin3', 'localhost:8888', '6DFCE51B15F97EF65BC6FEE41A77A1BB4B7E7515C3D8632E23A55257481D8DAC', '/county-asm-state', 200, '2024-06-13 19:45:04.652669', 0, 240667);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240673, 'countyadmin3', 'localhost:8888', '103AFA45F1D13843E741C32479EDF0564A87D79F0F1B9869617A0613F643228F', '/county-asm-state', 200, '2024-06-13 19:45:09.695784', 0, 240671);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240676, 'countyadmin3', 'localhost:8888', '5F08AA98682762D7413B1CBB3B5656E07AF17AA0D17A2F36DB65DF95BB977E56', '/audit-board-asm-state', 200, '2024-06-13 19:45:14.736727', 0, 240675);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240681, 'countyadmin3', 'localhost:8888', '332B5434F570CB832817044E07C838205454783754BC4A63CE9D465A601B90AF', '/county-asm-state', 200, '2024-06-13 19:45:19.776811', 0, 240679);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240684, 'countyadmin3', 'localhost:8888', 'B191E35395539DD5EAB8A6F46A7F32D7289B4A6B0F1D8AFC0C0E449DC8E278DE', '/audit-board-asm-state', 200, '2024-06-13 19:45:24.814639', 0, 240683);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240689, 'countyadmin3', 'localhost:8888', '7F385EF0A6A667E1C88F339071E213F7487F481DEE0A1C8FFCB92D77167BAB52', '/county-asm-state', 200, '2024-06-13 19:45:29.853948', 0, 240687);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240692, 'countyadmin3', 'localhost:8888', 'EC8B4A603B0D2383AC6BEE965C0847BE055E232388F5A45A4D06061868DFA961', '/audit-board-asm-state', 200, '2024-06-13 19:45:34.899016', 0, 240691);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240697, 'countyadmin3', 'localhost:8888', 'D770B1183918C8A281BA8EFC5F85E0422BF9A49A901C935549234CFF2CF49BA9', '/county-asm-state', 200, '2024-06-13 19:45:39.946958', 0, 240695);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240621, 'countyadmin2', 'localhost:8888', '617395DAE43B8C788BC43D37F92E8B698F50F8EC34AE3176F6D0E553C4CE1734', '/county-asm-state', 200, '2024-06-13 19:44:27.130223', 0, 240619);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240625, 'countyadmin2', 'localhost:8888', '489030ECAD816F66BCEB8B71A213127AB036F9985129382F3EA21690B9829DCD', '/audit-board-asm-state', 200, '2024-06-13 19:44:27.182685', 0, 240623);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240642, 'countyadmin2', 'localhost:8888', 'CEAD96B435E2E3859E3CE6D979F16FDC3F4215627505F2A44281948EB4C2CFAE', '/county-asm-state', 200, '2024-06-13 19:44:30.474362', 0, 240628);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240646, 'countyadmin2', 'localhost:8888', 'E653E475BBEBF0659025840A4C404CAD4FD5AC857786F7327618DF1B698A68B0', '/county-asm-state', 200, '2024-06-13 19:44:35.540469', 0, 240644);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240652, 'countyadmin3', 'localhost:8888', '57A31F46E5666D317C01D9C20C3E698A2058691BCAAC9D20D4C1AECAB5FC5D78', '/audit-board-asm-state', 200, '2024-06-13 19:44:44.363694', 0, 240651);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240657, 'countyadmin3', 'localhost:8888', '4B700A395ABEFD997385FAD1178E8258C5FD38FFA25E32130A048AF9F0EA5719', '/county-asm-state', 200, '2024-06-13 19:44:49.46066', 0, 240655);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240660, 'countyadmin3', 'localhost:8888', 'A7E76A32F83039DA403BCC6E5A4328C87A5ADC4501E3A0D91CFC44155CD29715', '/audit-board-asm-state', 200, '2024-06-13 19:44:54.547106', 0, 240659);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240665, 'countyadmin3', 'localhost:8888', 'A72EEAA3E08F42FB748F0C4B8897F11936905233C8156A3E3C24922ECC89111A', '/county-asm-state', 200, '2024-06-13 19:44:59.60689', 0, 240663);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240668, 'countyadmin3', 'localhost:8888', '8E65C00361CCC7EA9B2FB7B68DD0D767785443D794FDF1A9A19233DDC741FBB0', '/audit-board-asm-state', 200, '2024-06-13 19:45:04.652686', 0, 240667);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240672, 'countyadmin3', 'localhost:8888', '66B9A14BE5E97F97C53329A1D9D3E31C59DDC8B3E64F65009480E466660F7D1C', '/audit-board-asm-state', 200, '2024-06-13 19:45:09.694479', 0, 240671);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240677, 'countyadmin3', 'localhost:8888', '393AAA26F0E4CDFADB575253ADE5B0938A89DD89FCA96CB4D037DE1849A4F8C1', '/county-asm-state', 200, '2024-06-13 19:45:14.737352', 0, 240675);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240680, 'countyadmin3', 'localhost:8888', 'A14985C312BCDEA77945388AD9B1B097E0C26E348A3C1E01ECFC3BA893A6F1F8', '/audit-board-asm-state', 200, '2024-06-13 19:45:19.776176', 0, 240679);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240685, 'countyadmin3', 'localhost:8888', '0E01F7B0E95C8D4A959206A02CD85D093F47E5EA071C76BE9F2E22A07D45A689', '/county-asm-state', 200, '2024-06-13 19:45:24.815356', 0, 240683);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240688, 'countyadmin3', 'localhost:8888', 'B113EE30C6F05D10B88863893B63F6465699E689763FF269CCEFE20D3BD895C8', '/audit-board-asm-state', 200, '2024-06-13 19:45:29.853566', 0, 240687);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240693, 'countyadmin3', 'localhost:8888', '0AA20CB2670250A7DF846457BF7B08866E3629F1ED815B0420360F52B3604616', '/county-asm-state', 200, '2024-06-13 19:45:34.902029', 0, 240691);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240696, 'countyadmin3', 'localhost:8888', '542597580953578493CC553BBB12F319CCEE1271471967DF7BC7E93896EA1FE5', '/audit-board-asm-state', 200, '2024-06-13 19:45:39.945768', 0, 240695);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240623, 'countyadmin2', 'localhost:8888', 'E34E16EF4D03826E8406DE1F05B9565D91ED74BF0261FDCFBA531B90DD9AA0DC', '/import-cvr-export', 200, '2024-06-13 19:44:27.142147', 0, 240619);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240624, 'countyadmin2', 'localhost:8888', '5AB6800616200D490ADBD795E5F1B707BB8D94651FE634B31881A51153F883B5', '/contest/county', 200, '2024-06-13 19:44:27.178265', 0, 240623);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240626, 'countyadmin2', 'localhost:8888', '59C1C282809BE238E7B6576DBB284BCD420BC5C26DC4A2EBA89E3D8D483E9B55', '/county-asm-state', 200, '2024-06-13 19:44:27.191429', 0, 240625);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240643, 'countyadmin2', 'localhost:8888', '224A417097F23E4757C2B18353186E9B1B89AFE6AD6A9D05C250E41C80D8AB20', '/county-dashboard', 200, '2024-06-13 19:44:30.49056', 0, 240628);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240644, 'countyadmin2', 'localhost:8888', 'F1F21CF52B6E382D8D6BC80EF91DF8F7F96BBFAC8858669A59E40C38498DA05D', '/contest/county', 200, '2024-06-13 19:44:30.545116', 0, 240643);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240647, 'countyadmin2', 'localhost:8888', 'D059F6E8E274ED0C004BD320624B8E0A43D0F2028F6E02961C044138C2F91660', '/county-dashboard', 200, '2024-06-13 19:44:35.550769', 0, 240644);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240648, 'countyadmin2', 'localhost:8888', 'DD1ADE26090428FB0349BA376EBC5EA06F32E17D39CED5247230847EEF16CC96', '/contest/county', 200, '2024-06-13 19:44:35.57997', 0, 240647);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240649, '(unauthenticated)', 'localhost:8888', '6C822CCC14C69D85D894DF16204BCAFA2954E852F0EA999BB29C8592BD32F5D6', '/unauthenticate', 200, '2024-06-13 19:44:36.852436', 0, 240648);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240650, 'countyadmin3', 'localhost:8888', '34F8CE7EEBB63BEB99F7782DDA91BD2EA63EA27C084103863A040F6DCA5021DE', '/auth-admin', 200, '2024-06-13 19:44:42.974416', 0, 240649);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240651, 'countyadmin3', 'localhost:8888', '2FEC91DC6A540F749D8EBF8FF474F4D0918A1B5BDE5B5B40D04A71B7A798405B', '/auth-admin', 200, '2024-06-13 19:44:44.31411', 0, 240650);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240654, 'countyadmin3', 'localhost:8888', 'F32D0B86B28F269E2F9ED43DE42699D360852AA00DC15CDECB8C069234ED3685', '/county-dashboard', 200, '2024-06-13 19:44:44.383064', 0, 240651);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240655, 'countyadmin3', 'localhost:8888', 'E1DA44839583C6A5362F82B0312AC9F103CC10B608274F8E675DF58948F85E0C', '/contest/county', 200, '2024-06-13 19:44:44.452052', 0, 240654);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240658, 'countyadmin3', 'localhost:8888', '605526FF0F6115E3233572397A0F50B852692D1BBDD31134D03696AF87F3555D', '/county-dashboard', 200, '2024-06-13 19:44:49.470602', 0, 240655);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240659, 'countyadmin3', 'localhost:8888', '9515345B2E787520B4864F759E9A9E6C5A1F30BA9C9E712A47A65D357F66B7B1', '/contest/county', 200, '2024-06-13 19:44:49.533189', 0, 240658);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240662, 'countyadmin3', 'localhost:8888', 'A14F20AEF90A5624C04858D37556EEEE3CA76CF2E78F9E0DAB61153F4414FDCA', '/county-dashboard', 200, '2024-06-13 19:44:54.547722', 0, 240659);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240663, 'countyadmin3', 'localhost:8888', '5164C409CEF9360CC0D457DFE24E66519B9419EB0A6D5894A3E6EEBC6F1DBEE4', '/contest/county', 200, '2024-06-13 19:44:54.601277', 0, 240662);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240666, 'countyadmin3', 'localhost:8888', '5B1F8E4A990355755AE13547F79376EC87CD013BD12E0DFB5AD9A1A53F0F081A', '/county-dashboard', 200, '2024-06-13 19:44:59.612772', 0, 240663);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240667, 'countyadmin3', 'localhost:8888', '0B97213B7AF78CE6FCA1D92161141DEAC94B2C887AFC582FFFAB845EBA6E5D27', '/contest/county', 200, '2024-06-13 19:44:59.647707', 0, 240666);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240670, 'countyadmin3', 'localhost:8888', '9E56C03F102AE6576A69D0D0912D2483D3A3D3D1BA61E674D917F7B9F143A357', '/county-dashboard', 200, '2024-06-13 19:45:04.659739', 0, 240667);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240671, 'countyadmin3', 'localhost:8888', 'EF8555954938DC484A1DA1F3F7844135BFF537A2104FAB6E8660A56D971A0CE9', '/contest/county', 200, '2024-06-13 19:45:04.691839', 0, 240670);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240674, 'countyadmin3', 'localhost:8888', '8B66C2CE106103BCB09559C028B80CFAAD2AE213229E5D36C2B61544B1D036B2', '/county-dashboard', 200, '2024-06-13 19:45:09.703326', 0, 240671);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240675, 'countyadmin3', 'localhost:8888', 'E1E5899E18034DA31E8868F097F63D7B1939760A568A2ECE7D807C8FA406E726', '/contest/county', 200, '2024-06-13 19:45:09.73257', 0, 240674);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240678, 'countyadmin3', 'localhost:8888', 'F9691705E1D4AE48C63FC5617201CCF6DD960BE055221E0027885FDB7E3712AA', '/county-dashboard', 200, '2024-06-13 19:45:14.743586', 0, 240675);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240679, 'countyadmin3', 'localhost:8888', '9DD296A45355866138AD18E0510CA95EB80EABC17ABC71FDA5440BFFF113824E', '/contest/county', 200, '2024-06-13 19:45:14.772566', 0, 240678);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240682, 'countyadmin3', 'localhost:8888', 'A1C17BD538E91D2BB6780B0C4577DFABA2B65D33EA8EC29BEB7E74810FFC66F7', '/county-dashboard', 200, '2024-06-13 19:45:19.785472', 0, 240679);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240683, 'countyadmin3', 'localhost:8888', 'A69F1D89C999F148EFFAF6E853C77E9E63D643AEF3CD85516276CFDA0F00C640', '/contest/county', 200, '2024-06-13 19:45:19.810885', 0, 240682);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240686, 'countyadmin3', 'localhost:8888', '306D4C3475766DB0886C018865EB2657432C983B0FF03856230D5516A22E9012', '/county-dashboard', 200, '2024-06-13 19:45:24.82204', 0, 240683);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240687, 'countyadmin3', 'localhost:8888', '7E7BF2B3086281BD5D021DEFF7C16F0F9870C6564A667DE224048BF933D04D6F', '/contest/county', 200, '2024-06-13 19:45:24.848547', 0, 240686);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240690, 'countyadmin3', 'localhost:8888', '3C63F77A0F574772864BBECCB03C31116DE493728D11FD9F03986AE90537135E', '/county-dashboard', 200, '2024-06-13 19:45:29.863609', 0, 240687);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240691, 'countyadmin3', 'localhost:8888', '69EF2C5A1037D67781C6FB5C7F371B7250157CE8CC03384B7201E993F19B497D', '/contest/county', 200, '2024-06-13 19:45:29.894368', 0, 240690);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240694, 'countyadmin3', 'localhost:8888', 'AFF0B4137D0D6999BA92D7FB3732EB50B7503B6FA37859D639A48881563B0B31', '/county-dashboard', 200, '2024-06-13 19:45:34.909793', 0, 240691);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240695, 'countyadmin3', 'localhost:8888', 'B2467ECF88E269D97FC5A219E6CA2F2E40E99FEAA8BE35327CAA778F5BB22A0D', '/contest/county', 200, '2024-06-13 19:45:34.940089', 0, 240694);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240698, 'countyadmin3', 'localhost:8888', 'A264C2AB020EB07E10D7540FCB1C5CBB5C661172D09B0EBB60B968A515AA4D61', '/county-dashboard', 200, '2024-06-13 19:45:39.954951', 0, 240695);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240699, 'countyadmin3', 'localhost:8888', 'C28F68A40BE7037E161EDD8F0B3B712D55AD3A9AA96FDCCD3239AF60C0DB4202', '/contest/county', 200, '2024-06-13 19:45:39.984836', 0, 240698);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240700, 'countyadmin3', 'localhost:8888', '5E59E76B4D1F3FE9C446DC2B434CB469331455578B38F76B75D6D17445BE9D2D', '/audit-board-asm-state', 200, '2024-06-13 19:45:44.990286', 0, 240699);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240701, 'countyadmin3', 'localhost:8888', '865FDED5DB3288CEA901F6EC407400A344F2F13E2D52D0FF01299B00397937C9', '/county-asm-state', 200, '2024-06-13 19:45:44.990335', 0, 240699);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240702, 'countyadmin3', 'localhost:8888', '9D10CC6153DC22F8E22DA9BCC59E515AFDA831BA7323FA678A63FADB5FB34B1B', '/county-dashboard', 200, '2024-06-13 19:45:44.995624', 0, 240699);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240703, 'countyadmin3', 'localhost:8888', '46F8928EEB2A63D5F746B7DD3F51624FE1B32615610DE860BD60233236F515CB', '/contest/county', 200, '2024-06-13 19:45:45.024046', 0, 240702);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240704, 'countyadmin3', 'localhost:8888', 'CFE0F1F5F15F330B81E1D74BFB2F8332B8C036A855A2B157344DDE60A5252AEA', '/audit-board-asm-state', 200, '2024-06-13 19:45:50.028253', 0, 240703);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240705, 'countyadmin3', 'localhost:8888', 'A304DD7DB7B0294D706751BE3C070AB8461696456A6734CAAC72A05F01044D24', '/county-asm-state', 200, '2024-06-13 19:45:50.028504', 0, 240703);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240706, 'countyadmin3', 'localhost:8888', '26A60CB8016C125D72FC3A209CAF76DAC1A94F3F37C11AA0EB5315EF2CA6B10B', '/county-dashboard', 200, '2024-06-13 19:45:50.035936', 0, 240703);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240707, 'countyadmin3', 'localhost:8888', 'B77A76937D3EDFCA6E8865E85E42A5E085A1B13F862F6F77E5EC885842F5BA89', '/contest/county', 200, '2024-06-13 19:45:50.058419', 0, 240706);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240708, 'countyadmin3', 'localhost:8888', '6DC3732837233CF7BF687B4204E97C4FE63F78A5D92857DF92CD0330EB72F18B', '/audit-board-asm-state', 200, '2024-06-13 19:45:55.063516', 0, 240707);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240709, 'countyadmin3', 'localhost:8888', '1E8AB567B22B981184D10C404F39C931AE26A6AAA26BE4B35556DB6282369E1F', '/county-asm-state', 200, '2024-06-13 19:45:55.064176', 0, 240707);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240710, 'countyadmin3', 'localhost:8888', '760692C7AAFBA9915D171C9B59CB4DC2AF8010D328FDEE8380DC93406B69F6CB', '/county-dashboard', 200, '2024-06-13 19:45:55.070093', 0, 240707);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240711, 'countyadmin3', 'localhost:8888', '4A0B3B2E5A007C408F34A5F5E4729B312254561883A4691E66C12EB86F22F6AE', '/contest/county', 200, '2024-06-13 19:45:55.096909', 0, 240710);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240714, 'countyadmin3', 'localhost:8888', 'A82A2B9B9416FED409626DB06BC99B190E4E4261BD670C722FE87A75B9BEB7A9', '/county-dashboard', 200, '2024-06-13 19:46:00.110472', 0, 240711);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240715, 'countyadmin3', 'localhost:8888', 'FB5B467A7FEC30AC4D0A22A18469C56A52A65E5C5A32690DA9E6D4EB3C2415F7', '/contest/county', 200, '2024-06-13 19:46:00.144845', 0, 240714);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240718, 'countyadmin3', 'localhost:8888', 'B8E1EF75B2C42AFFCB83A93BE0244074E09073C033BC6D616B61403E14FDBE9F', '/county-dashboard', 200, '2024-06-13 19:46:05.159122', 0, 240715);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240719, 'countyadmin3', 'localhost:8888', 'C2E65CEA96CA3BADE8D7968F31C5D3F2490952457A056AE735658243CE1E5732', '/contest/county', 200, '2024-06-13 19:46:05.198124', 0, 240718);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240722, 'countyadmin3', 'localhost:8888', '9142918751F34C373D739A3C95A3C626BB66E17B8374FF1820CF76253BFEA913', '/county-dashboard', 200, '2024-06-13 19:46:10.204171', 0, 240719);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240723, 'countyadmin3', 'localhost:8888', 'CCD872087163D0A39EA5167A218A760DE765998F900A9EA1E9C62B357FB524A9', '/contest/county', 200, '2024-06-13 19:46:10.230234', 0, 240722);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240726, 'countyadmin3', 'localhost:8888', 'B131C7B0CE2F775D726D1DAAFA86D2917959D292A08FE771513DA72F8E685E58', '/county-dashboard', 200, '2024-06-13 19:46:15.245621', 0, 240723);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240727, 'countyadmin3', 'localhost:8888', 'C2A623B9B509076BB131BA353D90A545B2DB348BD7E893D2BC5852735FADC7B2', '/contest/county', 200, '2024-06-13 19:46:15.283118', 0, 240726);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240730, 'countyadmin3', 'localhost:8888', 'EF80332AA23FCC795BF44D7157F7FFB01EC76A900CE1A2D34651F6A14C6AB7D1', '/county-dashboard', 200, '2024-06-13 19:46:20.378833', 0, 240727);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240731, 'countyadmin3', 'localhost:8888', '373251B19EE8DEB5A43EA2E6D01B054220ACC90BB022E20E3B529FA19139D326', '/contest/county', 200, '2024-06-13 19:46:20.40994', 0, 240730);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240734, 'countyadmin3', 'localhost:8888', '3ABC3A75281F1FFD8A9F116310EE1925693E2AF4F5E50255944D8623502B4E25', '/county-dashboard', 200, '2024-06-13 19:46:25.418191', 0, 240731);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240735, 'countyadmin3', 'localhost:8888', '7EFC7C8CB5737D4DBC47589F8FAA263F555E70E209B2BA487DEF597721980478', '/contest/county', 200, '2024-06-13 19:46:25.4467', 0, 240734);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240737, 'countyadmin3', 'localhost:8888', 'C44A3B21C78F48015487905D52E508C4B6E068C8DEA33BCC02AD70FAB399B676', '/upload-file', 200, '2024-06-13 19:46:28.534909', 0, 240735);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240740, 'countyadmin3', 'localhost:8888', '2BFBCAF026479B1D43791264E57215F320384F4041FFFC4E434F2D809ABFCB18', '/county-dashboard', 200, '2024-06-13 19:46:28.575489', 0, 240737);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240744, 'countyadmin3', 'localhost:8888', 'A68D9719AE843E30235C258CBE48BA372104EFBE4EEE77F7BEA19C9B1EB00A0C', '/audit-board-asm-state', 200, '2024-06-13 19:46:28.627786', 0, 240742);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240753, 'countyadmin3', 'localhost:8888', '69C6AE8CF84638DD2D2C0A6E256B8DABBE63047F5A1A1227E45A65B927114F0E', '/county-dashboard', 200, '2024-06-13 19:46:30.464364', 0, 240746);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240754, 'countyadmin3', 'localhost:8888', 'CF235869BA00F176B6BE626911C5D2D09DC8E161E445DCAB38FAFF8DA707335D', '/contest/county', 200, '2024-06-13 19:46:30.535652', 0, 240753);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240757, 'countyadmin3', 'localhost:8888', '8312297E41EE3BFB26954D24D4BC461618E1F674BAE5D58C34EB46059F21FB36', '/county-dashboard', 200, '2024-06-13 19:46:35.543329', 0, 240754);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240758, 'countyadmin3', 'localhost:8888', '7FE9A42BEC2A24AAB396F8FCF5D74726501AEA49F57234AD696FBCC474765BD5', '/contest/county', 200, '2024-06-13 19:46:35.569629', 0, 240757);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240761, 'countyadmin3', 'localhost:8888', '9AB32D0C0F35681E5631B4C4973D16014F0841D4CEE5FAF4095916922A1DABFC', '/county-dashboard', 200, '2024-06-13 19:46:40.582091', 0, 240758);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240762, 'countyadmin3', 'localhost:8888', '0D5E2E8B6A1B66E81B709E8C25EAC4A613987DD35CE17BB49856C5574221CB5B', '/contest/county', 200, '2024-06-13 19:46:40.608197', 0, 240761);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240765, 'countyadmin3', 'localhost:8888', 'D688A825582E2AC45830F2C52623D323BEF075534C2AE0678E91EC7515018CA1', '/county-dashboard', 200, '2024-06-13 19:46:45.618481', 0, 240762);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240766, 'countyadmin3', 'localhost:8888', 'BE3CB219BF9BAF97BF58620C5682E1D6BE175D93BADA556A7FB9D71070E102BB', '/contest/county', 200, '2024-06-13 19:46:45.641646', 0, 240765);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240769, 'countyadmin3', 'localhost:8888', '3847F05961475472FFF722F2931CDA5A850B1EA6A0B2BE1659A3E5BABDD8708F', '/county-dashboard', 200, '2024-06-13 19:46:51.14719', 0, 240766);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240770, 'countyadmin3', 'localhost:8888', '4E6F949C1F225D9783F7B4CD148674C7049888A33B4FCCB1031E7B5409ECA2A9', '/contest/county', 200, '2024-06-13 19:46:51.178165', 0, 240769);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240773, 'countyadmin3', 'localhost:8888', '59216E5CA02CAACA1553A3FED2C04C1F0A2290CB9E1103FE76C657356C731326', '/county-dashboard', 200, '2024-06-13 19:46:56.187686', 0, 240770);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240774, 'countyadmin3', 'localhost:8888', '47339218B6713DC28AB2561B82BD92A69812C988503F79BAD5C3216369740EA7', '/contest/county', 200, '2024-06-13 19:46:56.21861', 0, 240773);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240777, 'countyadmin3', 'localhost:8888', '6EF5A1349DD38DEF608C63737EE9878B4A8BFF83C3D3F59C05084E3F2AFE4D30', '/county-dashboard', 200, '2024-06-13 19:47:01.232156', 0, 240774);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240778, 'countyadmin3', 'localhost:8888', 'DABA39BA48540314D92184A90DC13D42D4FFB7040DE05D2F7C09F45B1A0E0369', '/contest/county', 200, '2024-06-13 19:47:01.257364', 0, 240777);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240781, 'countyadmin3', 'localhost:8888', 'EC2B4EFCDA399FD20E64923EF167D21ECA57975B829E486043B5B5FF8733CD14', '/county-dashboard', 200, '2024-06-13 19:47:06.269421', 0, 240778);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240782, 'countyadmin3', 'localhost:8888', '9C4F32A973F9F2909BF64E661D62D3A40C4C43F1B71DB1733F1C90FE97367047', '/contest/county', 200, '2024-06-13 19:47:06.29493', 0, 240781);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240785, 'countyadmin3', 'localhost:8888', 'B4124B72982A7C7B733DB41FBFEEE9DE61389DC4F283BEDB9E78F2C46D37E1B8', '/county-dashboard', 200, '2024-06-13 19:47:11.304821', 0, 240782);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240786, 'countyadmin3', 'localhost:8888', '1F543979437A7C0A4415C2C3F899ADFA1D55233CE83F30BE3C0395192FBD203C', '/contest/county', 200, '2024-06-13 19:47:11.333801', 0, 240785);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240789, 'countyadmin3', 'localhost:8888', 'C3B9E76C6E2BBAD64D3666F3261EB933E0C3AF91A55388A01EAA902EDCAC605D', '/county-dashboard', 200, '2024-06-13 19:47:17.3188', 0, 240786);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240790, 'countyadmin3', 'localhost:8888', '8B90C522DFA17E427F2B83877FF70DB74683247F0989DE9617666B575FF9EB03', '/contest/county', 200, '2024-06-13 19:47:17.342564', 0, 240789);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240793, 'countyadmin3', 'localhost:8888', 'B5F97E2B05EEF636FE79B70CFD20B956CD3079D8B358FF6D7C87A073B5B3318A', '/county-dashboard', 200, '2024-06-13 19:47:23.340086', 0, 240790);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240794, 'countyadmin3', 'localhost:8888', '18183AC6DFED689CF8E07FA05357CE8B651C1A1F9C71B0D341E7A0FBFF4A716B', '/contest/county', 200, '2024-06-13 19:47:23.366869', 0, 240793);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240797, 'countyadmin3', 'localhost:8888', '899F9A73FB20F8BE6FD4CCB3739695A326EC60FA119A26FEDCDA83C396FBF193', '/county-dashboard', 200, '2024-06-13 19:47:29.370486', 0, 240794);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240798, 'countyadmin3', 'localhost:8888', 'C89643A5DB92ADB54063F4BABB0E3E2DEA60A7049E1AB0FBEBCCA0FA13A54830', '/contest/county', 200, '2024-06-13 19:47:29.402941', 0, 240797);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240801, 'countyadmin3', 'localhost:8888', '4A07CC80103428F12A471EA28BDFE1789685B4D7709A72A62084FCD26C83563F', '/county-dashboard', 200, '2024-06-13 19:47:34.414695', 0, 240798);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240802, 'countyadmin3', 'localhost:8888', '15B76A33B6875DFC638B553DD1DB805291637C62DE9A31F993DFD2A52C6DEFAD', '/contest/county', 200, '2024-06-13 19:47:34.439095', 0, 240801);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240805, 'countyadmin3', 'localhost:8888', 'A9200021898F690486D83DA6C928A43B99D821F86559A805CBFADA3E549B7B46', '/county-dashboard', 200, '2024-06-13 19:47:39.45194', 0, 240802);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240806, 'countyadmin3', 'localhost:8888', '853053327D02AEF6D80C419F17A2044972BC0BD3BD8E599A721E6FA0C5E4F18B', '/contest/county', 200, '2024-06-13 19:47:39.479389', 0, 240805);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240712, 'countyadmin3', 'localhost:8888', 'DDB06113F711A7052D8EBA2CD1C50E68D1E731DC3823CBDE40B34F98D934FAC9', '/audit-board-asm-state', 200, '2024-06-13 19:46:00.102051', 0, 240711);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240717, 'countyadmin3', 'localhost:8888', 'AB4C5E76D677EBC45491FCFE4CFC4741E5DE2B5B9F9B640BA308B73C914B3D46', '/county-asm-state', 200, '2024-06-13 19:46:05.149711', 0, 240715);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240721, 'countyadmin3', 'localhost:8888', '96AA79CBF86DCCF58FC030E293027F60BFFF6DA0B69932EAD03013E1737599DD', '/audit-board-asm-state', 200, '2024-06-13 19:46:10.197088', 0, 240719);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240724, 'countyadmin3', 'localhost:8888', 'CF3216E3EF3493202D1F021E1BB4CAADB9E46855D4E47B5631D4CF4F0F7E68AB', '/audit-board-asm-state', 200, '2024-06-13 19:46:15.236632', 0, 240723);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240728, 'countyadmin3', 'localhost:8888', 'F3018CEF096D0FF82DD88BD207256283FCADE41F81746F7EEEC8122E2BE37287', '/audit-board-asm-state', 200, '2024-06-13 19:46:20.37215', 0, 240727);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240733, 'countyadmin3', 'localhost:8888', '7FE52D9D9BB011D77CA387B476287543E4024709A4193D860D92C2619A23CDE3', '/county-asm-state', 200, '2024-06-13 19:46:25.412279', 0, 240731);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240738, 'countyadmin3', 'localhost:8888', '896661F66D64C48B89BA60320E15898024B056FE032D00B411FDCA36C4B48030', '/audit-board-asm-state', 200, '2024-06-13 19:46:28.561958', 0, 240737);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240713, 'countyadmin3', 'localhost:8888', '1DB48C3F2C1717BE61171625C73188325322199E8C34F03E949C7C48C8CDBABB', '/county-asm-state', 200, '2024-06-13 19:46:00.102678', 0, 240711);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240716, 'countyadmin3', 'localhost:8888', '8A6DC7315217C87F51FE17691431FB07DE4EC5FB33B4796D33432245C8D4E1AB', '/audit-board-asm-state', 200, '2024-06-13 19:46:05.14917', 0, 240715);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240720, 'countyadmin3', 'localhost:8888', 'E2B2399B1833A1F404CF63B9E443BDE06E14D9C8DCF870EA1283BC5B9C44E126', '/county-asm-state', 200, '2024-06-13 19:46:10.196971', 0, 240719);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240725, 'countyadmin3', 'localhost:8888', '609360B8B6F561F7CAD0C64C9E61BF96506A3DFA0E69328B8578F4360E21CA40', '/county-asm-state', 200, '2024-06-13 19:46:15.236824', 0, 240723);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240729, 'countyadmin3', 'localhost:8888', '5A325172A45F19A1110AB0221F20C2C41BB1BAC86762E736702AB0088BD09A6E', '/county-asm-state', 200, '2024-06-13 19:46:20.372486', 0, 240727);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240732, 'countyadmin3', 'localhost:8888', 'D231739C2D44C5C17867E303CBEDE8077512EBCCE735AF1138F5710E60B9D3A7', '/audit-board-asm-state', 200, '2024-06-13 19:46:25.411747', 0, 240731);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240739, 'countyadmin3', 'localhost:8888', '8C3E48DC9374FA9510B1E0885E4A0D8E79A98A007FE0A5666CE91641C57EA698', '/county-asm-state', 200, '2024-06-13 19:46:28.563666', 0, 240737);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240741, 'countyadmin3', 'localhost:8888', '0D12A06B6946B76F5A5154E1EAC26AC8E507DC5E9992C35DF5429E4EEE78E892', '/import-cvr-export', 200, '2024-06-13 19:46:28.578562', 0, 240737);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240742, 'countyadmin3', 'localhost:8888', '17928DCE843466FC07962DB784F87021AE84F4913D0AE244F3DB0D22273CC5B5', '/contest/county', 200, '2024-06-13 19:46:28.619637', 0, 240741);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240743, 'countyadmin3', 'localhost:8888', 'D719238231A64F39484648CF5769EE2E5B637BCA423EF10F56B4B362FAC7654F', '/county-asm-state', 200, '2024-06-13 19:46:28.627928', 0, 240742);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240745, 'countyadmin3', 'localhost:8888', '464797A72D836969406B5CDDA72F2666F7921FB0D52481D0A25EAD9DADC1785A', '/county-dashboard', 200, '2024-06-13 19:46:28.647479', 0, 240742);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240746, 'countyadmin3', 'localhost:8888', 'FEF59495F061E24C16B415B26900AEADFBC6C956CCF46CE99CD22DAD04742A38', '/contest/county', 200, '2024-06-13 19:46:28.667798', 0, 240745);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240751, 'countyadmin3', 'localhost:8888', 'DAD70581689BD9C311C83ABFD48015D635B5A47DA9691E27DBAA68A4A05E413A', '/audit-board-asm-state', 200, '2024-06-13 19:46:30.455058', 0, 240746);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240752, 'countyadmin3', 'localhost:8888', 'AA4DDF9BCE5136BC405ADCCDF8C014320AF67AB69B44D8D7A0789A2A2CD76313', '/county-asm-state', 200, '2024-06-13 19:46:30.455554', 0, 240746);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240755, 'countyadmin3', 'localhost:8888', 'B2CDD63672B2732D540084E73E16FBF24384114CEF71C8E898A160267FC9F01F', '/audit-board-asm-state', 200, '2024-06-13 19:46:35.534592', 0, 240754);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240756, 'countyadmin3', 'localhost:8888', '5709DBD2317A53FA34D27B5CDDEB4F6DAE5C1F74181BA1CACD94703CD6009CEE', '/county-asm-state', 200, '2024-06-13 19:46:35.535571', 0, 240754);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240759, 'countyadmin3', 'localhost:8888', '3A4EB3EDCCB0360899AC0CE4597B698D8A126CB8BEBC906E037ACD26D3B8CDFC', '/audit-board-asm-state', 200, '2024-06-13 19:46:40.573968', 0, 240758);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240760, 'countyadmin3', 'localhost:8888', '98BD7372DA66A4F2639B694A57D073B498D39C44DEC19FAF315DAC4E0C1080C1', '/county-asm-state', 200, '2024-06-13 19:46:40.574221', 0, 240758);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240763, 'countyadmin3', 'localhost:8888', '6029E41CA59F8924F067E8993F062845D3578D681AB1F390E91B00715148AE09', '/audit-board-asm-state', 200, '2024-06-13 19:46:45.610826', 0, 240762);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240764, 'countyadmin3', 'localhost:8888', '2F0953EB250A9EC10027BC478850F5C57FB0A2B7680BD4F76861EEA57DA23929', '/county-asm-state', 200, '2024-06-13 19:46:45.611515', 0, 240762);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240767, 'countyadmin3', 'localhost:8888', 'C5EEC4F3CE7C1194E42B0509DA14D22BC9738B4CCAF0186146AFBBC9BE4B6454', '/audit-board-asm-state', 200, '2024-06-13 19:46:51.138312', 0, 240766);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240768, 'countyadmin3', 'localhost:8888', '1EFCA1C539C8672C08DAA47165EA182CE7A13E71D917C7AF50184A4A95B3FA83', '/county-asm-state', 200, '2024-06-13 19:46:51.13844', 0, 240766);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240771, 'countyadmin3', 'localhost:8888', '6D459AC20EE0C06A72823961AC0A5F7139B90361647EB6158AAE20897A78F797', '/audit-board-asm-state', 200, '2024-06-13 19:46:56.179126', 0, 240770);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240772, 'countyadmin3', 'localhost:8888', '35DD23CA8E5641AAA1C01CF92D55FC84B08E2FF1C086589331617DB559315B96', '/county-asm-state', 200, '2024-06-13 19:46:56.180264', 0, 240770);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240775, 'countyadmin3', 'localhost:8888', 'F8A6FE31E67FF4F87D47437044F12E35F5A28DFA3948A5DC5BCEB42502AA9028', '/audit-board-asm-state', 200, '2024-06-13 19:47:01.223706', 0, 240774);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240776, 'countyadmin3', 'localhost:8888', 'FA50B671CFD86A46CB52EDDC6EE2C633346C634115FE146E57AA7F204AF75D00', '/county-asm-state', 200, '2024-06-13 19:47:01.223974', 0, 240774);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240779, 'countyadmin3', 'localhost:8888', '2358994D3A72E61D422472FF64200510DE1D2EB497BFE603A301F7BCA016880C', '/audit-board-asm-state', 200, '2024-06-13 19:47:06.261534', 0, 240778);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240780, 'countyadmin3', 'localhost:8888', '416D6365E9DA22394191FBC524246837A95CD6F4E1FED4A24E59CBC946C90657', '/county-asm-state', 200, '2024-06-13 19:47:06.261821', 0, 240778);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240783, 'countyadmin3', 'localhost:8888', '4C98EB43B05E2796B20818D5F1B9104FE8B635494A67BBF6FDE31B2089ACBF01', '/audit-board-asm-state', 200, '2024-06-13 19:47:11.29733', 0, 240782);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240784, 'countyadmin3', 'localhost:8888', '6A8A233DBE2C7398D2DD358D1AEA2F499201F1580AC5AAB86A69D46AD9F4AFFA', '/county-asm-state', 200, '2024-06-13 19:47:11.298069', 0, 240782);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240787, 'countyadmin3', 'localhost:8888', 'BDE96E01181F6A929058DA3C482AD81382F468F0BECD99ABDCF28E758303EF84', '/audit-board-asm-state', 200, '2024-06-13 19:47:17.311163', 0, 240786);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240788, 'countyadmin3', 'localhost:8888', '3145FC5977F6223B77223FE2102B37BCAD8492BDD1B5A0E4178B6A0BDE2EF57A', '/county-asm-state', 200, '2024-06-13 19:47:17.312453', 0, 240786);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240791, 'countyadmin3', 'localhost:8888', 'B5E3E75406AD5EBC92B4E2393D3D217996C79A2B82826CD5A859E6CEE93C3039', '/audit-board-asm-state', 200, '2024-06-13 19:47:23.332582', 0, 240790);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240792, 'countyadmin3', 'localhost:8888', 'EDD7999124B4ADD04CC765CEFDE44AC2BA55EFC3CD4F72DC0F2DFCC345F2192C', '/county-asm-state', 200, '2024-06-13 19:47:23.332879', 0, 240790);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240795, 'countyadmin3', 'localhost:8888', '33D581D0C81ACEE5575757E364AD0E8AB85076EB43E0588332B2736832D13FFD', '/audit-board-asm-state', 200, '2024-06-13 19:47:29.361946', 0, 240794);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240796, 'countyadmin3', 'localhost:8888', '81966FEA68EA2D823AE66AB07FCEEEDAA5E0D5364887837A8F42A8FDB4C66EBF', '/county-asm-state', 200, '2024-06-13 19:47:29.362834', 0, 240794);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240799, 'countyadmin3', 'localhost:8888', 'DEF169D478D11E8B936234A4E9475F1E3B65E2B79721291B9855F88B1872B999', '/audit-board-asm-state', 200, '2024-06-13 19:47:34.405939', 0, 240798);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240800, 'countyadmin3', 'localhost:8888', 'F6383FE60CEF703CF9800BDFD8B2194B6A3FD85E88B2D75950309FAEC0F5E5D0', '/county-asm-state', 200, '2024-06-13 19:47:34.408306', 0, 240798);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240804, 'countyadmin3', 'localhost:8888', '784293E728AB3646E0BA6AFDE76691042C9989C47B4E18474C0B5993DA5DDEFF', '/county-asm-state', 200, '2024-06-13 19:47:39.443956', 0, 240802);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240803, 'countyadmin3', 'localhost:8888', '411C0D85DEEB29906ECB08ED599F605606C6F83AAC264560028423A36AECC81C', '/audit-board-asm-state', 200, '2024-06-13 19:47:39.443881', 0, 240802);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240807, 'countyadmin3', 'localhost:8888', 'EBF517322421BF454C87A814931F03807B20A427B1F10C3B83CABDA3891B1094', '/audit-board-asm-state', 200, '2024-06-13 19:47:45.473904', 0, 240806);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240808, 'countyadmin3', 'localhost:8888', '69963E69FCCC867368CB54913295D752D6B18D2D3671778D87E709DF16012BBB', '/county-asm-state', 200, '2024-06-13 19:47:45.474494', 0, 240806);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240809, 'countyadmin3', 'localhost:8888', 'DE5269CE17F05065D1E744D301F4FA4E68A04A81EEC7A503FA9E6921E597E4FC', '/county-dashboard', 200, '2024-06-13 19:47:45.482329', 0, 240806);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240810, 'countyadmin3', 'localhost:8888', '495CF16457F3A30280E12BCE18E95EDD5CFD1DF7B43AB5FF27FF851ADDA88A3A', '/contest/county', 200, '2024-06-13 19:47:45.515542', 0, 240809);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240811, 'countyadmin3', 'localhost:8888', '2DC38981A65B205E4C44D6F0AE72EB90787A8EDEB874848907975E724B7187F3', '/county-dashboard', 200, '2024-06-13 19:47:50.525707', 0, 240810);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240812, 'countyadmin3', 'localhost:8888', 'B24CBD9133C1D1AAF33006B21AC9A9FF790C8ED0683260A08D7FC6A6B2357FB6', '/county-asm-state', 200, '2024-06-13 19:47:50.527047', 0, 240810);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240814, 'countyadmin3', 'localhost:8888', '28F9B65D5BA45596BDCE2C5A301A1829147359292CCBF984332D707A16ED1798', '/contest/county', 200, '2024-06-13 19:47:50.551389', 0, 240813);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240817, 'countyadmin3', 'localhost:8888', '3A8D0E7D1A2B8A8F4E87B83FD81D812231570AD34F3E428D01DDEA7499E54E5B', '/county-dashboard', 200, '2024-06-13 19:47:56.560931', 0, 240814);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240818, 'countyadmin3', 'localhost:8888', '68D863DCD45EC5C821BE8ED8916510776517799860A1EC35E0897285399A1372', '/contest/county', 200, '2024-06-13 19:47:56.590841', 0, 240817);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240821, 'countyadmin3', 'localhost:8888', '21A312A86C6C7AD49790A44DDE25E86AFAC2140F07D2A4D6F4A0E050FC79DD63', '/county-dashboard', 200, '2024-06-13 19:48:01.668047', 0, 240818);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240822, 'countyadmin3', 'localhost:8888', '92B3B4B887F9F35F713697F9F6D12BE7E53BFEEBB26693222B67E8537AE830A6', '/contest/county', 200, '2024-06-13 19:48:01.691799', 0, 240821);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240825, 'countyadmin3', 'localhost:8888', '95B87359658E429485BCE5CDC04E3379527467C72E81EFDB13404BB9ED3E8510', '/county-dashboard', 200, '2024-06-13 19:48:07.679708', 0, 240822);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240826, 'countyadmin3', 'localhost:8888', '657CAE5F7FBB2F08EFA2923A52D0F5775BB6F0DF8FE8468B908DD0A0C501E428', '/contest/county', 200, '2024-06-13 19:48:07.704427', 0, 240825);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240829, 'countyadmin3', 'localhost:8888', '28F1DBCDE820A70FECAB8B6BB350528555AE80EA5005D22C77B6B86FF2765E33', '/county-dashboard', 200, '2024-06-13 19:48:13.689111', 0, 240826);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240830, 'countyadmin3', 'localhost:8888', '9494B1662BA9C01A6DD9F51530C231AEDA840F206DE8D0B43568B98244BB9996', '/contest/county', 200, '2024-06-13 19:48:13.714652', 0, 240829);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240813, 'countyadmin3', 'localhost:8888', '1773AABC5ED097C965AE52D79957F8D68E67CFFBD1F702887B7CF3084C9D5898', '/audit-board-asm-state', 200, '2024-06-13 19:47:50.527519', 0, 240810);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240815, 'countyadmin3', 'localhost:8888', '3B15E5DAA30CE522A599CA547A971EAA9D592D00C0EE4C9C84D3C336C0AC964C', '/audit-board-asm-state', 200, '2024-06-13 19:47:56.550406', 0, 240814);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240820, 'countyadmin3', 'localhost:8888', '2D8F18E58C97AA1BFC3B504A24A06937E79372E4394AF99EA59C21DDB61813B0', '/county-asm-state', 200, '2024-06-13 19:48:01.658697', 0, 240818);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240824, 'countyadmin3', 'localhost:8888', '1A7D3E740DAFF7DB82313284A5C24E0CDAFB4BC19E0A63C98439C25C870DB466', '/audit-board-asm-state', 200, '2024-06-13 19:48:07.669019', 0, 240822);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240827, 'countyadmin3', 'localhost:8888', 'EC6FCC74495A4C84D8E15CA3169B8DB6CD151E6501494352448622499E473C70', '/county-asm-state', 200, '2024-06-13 19:48:13.680782', 0, 240826);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240816, 'countyadmin3', 'localhost:8888', 'CA7DA6121311ECFBC0F65E412F1CBB639B60F82C13F27A61278D7E3D952060CB', '/county-asm-state', 200, '2024-06-13 19:47:56.551161', 0, 240814);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240819, 'countyadmin3', 'localhost:8888', '88BD610BAAB6FD5125B3F339AA846A65A073C16421CE5A9F047FA5837275F422', '/audit-board-asm-state', 200, '2024-06-13 19:48:01.658276', 0, 240818);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240823, 'countyadmin3', 'localhost:8888', '5EF82769B019E897ADD7739A68AF365F933288D377034F3D1540F0A96A09A699', '/county-asm-state', 200, '2024-06-13 19:48:07.668968', 0, 240822);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240828, 'countyadmin3', 'localhost:8888', '163290AF5C54E29E2DF31E04C937BEBDF77849613AF346813677AC9C7F2D94CE', '/audit-board-asm-state', 200, '2024-06-13 19:48:13.680635', 0, 240826);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240831, 'countyadmin3', 'localhost:8888', '98F6C0F4CCE2339772B408577FB776964F0B4A0C69A271550939E2CE256D7AD1', '/county-asm-state', 200, '2024-06-13 19:48:19.689974', 0, 240830);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240832, 'countyadmin3', 'localhost:8888', '5A21ED64C0144A452FDC5E4B2EA48CDF04D39F727459EB3FA69A44AF2B50EBD7', '/audit-board-asm-state', 200, '2024-06-13 19:48:19.689974', 0, 240830);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240833, 'countyadmin3', 'localhost:8888', '53E0366A1B672E96A30B633F78C0A7CBAA6DFD8D14C0E71996FC17B85A3B81EA', '/county-dashboard', 200, '2024-06-13 19:48:19.701833', 0, 240830);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240834, 'countyadmin3', 'localhost:8888', '2AAA11AE0BFD80650ADA325D20C0E0FC4216FF150797790766B268421E86BA5D', '/contest/county', 200, '2024-06-13 19:48:19.730787', 0, 240833);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240835, 'countyadmin3', 'localhost:8888', '86627CE44CD518D558CA80DD2D8D0E58C74EB080F55E289F1318FF629C1C90CB', '/county-asm-state', 200, '2024-06-13 19:48:25.727995', 0, 240834);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240836, 'countyadmin3', 'localhost:8888', '0A779564FD20FD5B09717570A867F1AC23DCFECA006B9B3A9FF1F1E7A1943B80', '/audit-board-asm-state', 200, '2024-06-13 19:48:25.727928', 0, 240834);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240837, 'countyadmin3', 'localhost:8888', 'C7C74D0169C69416912FAEBA831C5145B702155C7467FA9646A848CD062BADC6', '/county-dashboard', 200, '2024-06-13 19:48:25.737683', 0, 240834);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240838, 'countyadmin3', 'localhost:8888', 'DFB13E1AC1F4208938C2C657F83DE1F7BD8F8AD47904F5A70520ED5430C10934', '/contest/county', 200, '2024-06-13 19:48:25.761797', 0, 240837);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240839, 'countyadmin3', 'localhost:8888', 'FBB39F2CDF4B25D30425FAD548AF1607A2C96E9553DD893ED6CC6BA692F536D4', '/audit-board-asm-state', 200, '2024-06-13 19:48:31.73728', 0, 240838);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240840, 'countyadmin3', 'localhost:8888', 'DFD846FA246F44BDCA8030E50D99B747083BC2EB41512DB1B41F85E11BAAA338', '/county-asm-state', 200, '2024-06-13 19:48:31.737761', 0, 240838);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240841, 'countyadmin3', 'localhost:8888', 'E6997147FFE497B9ABB8F1B65A0C0BB1FA125523F9E56E9B54740D80F90B8BB3', '/county-dashboard', 200, '2024-06-13 19:48:31.750308', 0, 240838);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240842, 'countyadmin3', 'localhost:8888', '98655B0AB87B8201F7BB03B310CA4E454224D97054ECBBD553E3C634CF04E3BB', '/contest/county', 200, '2024-06-13 19:48:31.777097', 0, 240841);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240843, 'countyadmin3', 'localhost:8888', '59F9B73F5CC8569E61BCC1604F2BF8B2D19A50C821C1EAA27D7904F89CE5831D', '/audit-board-asm-state', 200, '2024-06-13 19:48:37.747685', 0, 240842);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240844, 'countyadmin3', 'localhost:8888', '804E8800A87DECAA9CEB1932D478E2A981202246EE8148D4E813FF335F9F76EB', '/county-asm-state', 200, '2024-06-13 19:48:37.748184', 0, 240842);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240845, 'countyadmin3', 'localhost:8888', '0612674B478EA055275CF8F8D37C676AAFB623E0E1475A12080D1D2A052F65C4', '/county-dashboard', 200, '2024-06-13 19:48:37.756387', 0, 240842);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240846, 'countyadmin3', 'localhost:8888', 'DBEF26CB684D9A86860F872D3A1AE956D23272B76ABF789E4F23A26359162181', '/contest/county', 200, '2024-06-13 19:48:37.78212', 0, 240845);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240847, 'countyadmin3', 'localhost:8888', 'B2FC92E58A319958C034AAE785869796C5E977618B3B62F6CEA46E7E75CD0ED1', '/audit-board-asm-state', 200, '2024-06-13 19:48:43.770955', 0, 240846);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240848, 'countyadmin3', 'localhost:8888', 'F44F0D1DDF2BAECFBA45030F710D694417361AAFF519688542C83DFCB5337BF1', '/county-asm-state', 200, '2024-06-13 19:48:43.771109', 0, 240846);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240849, 'countyadmin3', 'localhost:8888', 'C99A6A30F833802CA122AEAD1C0B712FB7E257DB1581919C9EFF56D5DCAFE6BB', '/county-dashboard', 200, '2024-06-13 19:48:43.781917', 0, 240846);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240850, 'countyadmin3', 'localhost:8888', '76951608DB88E06CD107E52A688799C6E697DBDEB9E05446397891FC2824028F', '/contest/county', 200, '2024-06-13 19:48:43.803661', 0, 240849);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240851, 'countyadmin3', 'localhost:8888', 'E45AC03D0ADEA5791B826137BB2EED3D246F289EBCEE47F49A6EDF6F677E4BE4', '/audit-board-asm-state', 200, '2024-06-13 19:48:49.781255', 0, 240850);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240852, 'countyadmin3', 'localhost:8888', 'BFBCA145463377113BB7A6819F3C1C7966090B8BFF500503109902190E458A6D', '/county-asm-state', 200, '2024-06-13 19:48:49.781591', 0, 240850);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240853, 'countyadmin3', 'localhost:8888', 'DDDE8B4EE3450CEBC3C83D1ADE69CDEB7483F68F13ADB935DBEBFCD2A8B6CFD3', '/county-dashboard', 200, '2024-06-13 19:48:49.789844', 0, 240850);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240854, 'countyadmin3', 'localhost:8888', '306744DAEF4B4E48A1221B12BEF2ED4548F147F14708B628DE2CB1ECA68D11D0', '/contest/county', 200, '2024-06-13 19:48:49.815941', 0, 240853);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240855, 'countyadmin3', 'localhost:8888', '58864373EA9D3117E6FC942FF36EA0E0D8EAE4508C97EE019445F090D0F3BE1C', '/county-asm-state', 200, '2024-06-13 19:48:55.802225', 0, 240854);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240856, 'countyadmin3', 'localhost:8888', '4F879717538FCBD66E124A896A168DDE3AD5014D9301776F35BBD30BBF77C5D4', '/audit-board-asm-state', 200, '2024-06-13 19:48:55.802212', 0, 240854);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240857, 'countyadmin3', 'localhost:8888', 'E666537100C3C488EDF3255685F30A37E850A33357D6FE0A53C9789E80110595', '/county-dashboard', 200, '2024-06-13 19:48:55.810221', 0, 240854);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240858, 'countyadmin3', 'localhost:8888', '340529DE54B8D12D1F76268126D3DB7C341CAB54445E1C9D201B21F1957F6BF2', '/contest/county', 200, '2024-06-13 19:48:55.835704', 0, 240857);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240859, 'countyadmin3', 'localhost:8888', 'B99532B8898DC1C456CB466567CC77FC739E43C707E7D220538FC56A58A56218', '/audit-board-asm-state', 200, '2024-06-13 19:49:01.809747', 0, 240858);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240860, 'countyadmin3', 'localhost:8888', '7DCEEE5CEC83712D50E051015C6DE446F1E8C1B1B0FFAA46556ED3BF8EBD7FF6', '/county-asm-state', 200, '2024-06-13 19:49:01.810284', 0, 240858);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240861, 'countyadmin3', 'localhost:8888', 'D7DF2EBED89C9E430D9EC3EF59741376C3BF876C2F85BBBEB9502F3013AA5A84', '/county-dashboard', 200, '2024-06-13 19:49:01.819723', 0, 240858);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240862, 'countyadmin3', 'localhost:8888', '84753885E005F2F1D96AF01CC465A78C9F43FAF430E913BD99FC10A8F3848524', '/contest/county', 200, '2024-06-13 19:49:01.84526', 0, 240861);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240863, 'countyadmin3', 'localhost:8888', '79FAE5A178F03ACA408425505265632A29451F3F0BA9CC7C1F6E721CFE6B9B3A', '/county-asm-state', 200, '2024-06-13 19:49:06.851235', 0, 240862);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240864, 'countyadmin3', 'localhost:8888', '406B2765B37FD36D01523994917AF1E7069E8C13AE2F2CB58E7721DD95EBBB59', '/audit-board-asm-state', 200, '2024-06-13 19:49:06.851499', 0, 240862);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240865, 'countyadmin3', 'localhost:8888', '1E21520FEEC8A7994D43A6BCF7A4E402C82D08DE4D0F0D21F84E9F622A1A5E18', '/county-dashboard', 200, '2024-06-13 19:49:06.858099', 0, 240862);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240866, 'countyadmin3', 'localhost:8888', '979376F2F988689241C9ABDC0C403AAEB94148EEE38F9D5A8286C994B8EE202B', '/contest/county', 200, '2024-06-13 19:49:06.88333', 0, 240865);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240867, 'countyadmin3', 'localhost:8888', '84615B58396E4EB992DC1C14165684DD1ED21897E9AE2BF2ED1596BD198A38DF', '/audit-board-asm-state', 200, '2024-06-13 19:49:12.879302', 0, 240866);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240868, 'countyadmin3', 'localhost:8888', '2D7E054B3505231B4FBDB8DDA3DA8ECE5291F6BF979EB940B463C516B274E4C5', '/county-asm-state', 200, '2024-06-13 19:49:12.879616', 0, 240866);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240869, 'countyadmin3', 'localhost:8888', 'E9E8FC1DE2D1CEEAA0B660AE12918824C07DC4C6E552D843A3F0415DF79A95FA', '/county-dashboard', 200, '2024-06-13 19:49:12.886065', 0, 240866);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240870, 'countyadmin3', 'localhost:8888', '4F18E19A240841B212641D0FCF27297A52E8A719243E9F74E9D421C6D3B5C228', '/contest/county', 200, '2024-06-13 19:49:12.911963', 0, 240869);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240871, 'countyadmin3', 'localhost:8888', '7C1B97ACB020942196087011C38D16DA936839EB64A760A503EA683EEAA2B6C0', '/audit-board-asm-state', 200, '2024-06-13 19:49:17.915467', 0, 240870);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240872, 'countyadmin3', 'localhost:8888', '71D579B1DD5C1AA9F1ECE9DA8AB863033A08928980B8C4E3272B4F682F909C9B', '/county-asm-state', 200, '2024-06-13 19:49:17.916286', 0, 240870);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240875, 'countyadmin3', 'localhost:8888', '85717847AED38AEE1DF17E1375C9F2BD75D2498537241BF60409C5CA9E7A47FD', '/audit-board-asm-state', 200, '2024-06-13 19:49:22.948493', 0, 240874);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240879, 'countyadmin3', 'localhost:8888', 'AC4AD4C655A25EA5A4C2F6FF0B7946C2C9A491D93E7B5BF532FC28F775F06628', '/county-asm-state', 200, '2024-06-13 19:49:27.9842', 0, 240878);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240873, 'countyadmin3', 'localhost:8888', '54727A20602F9FD9BE5AC5D91FAFC4094B245D955890C6ACEB2CB0F9BAE539AD', '/county-dashboard', 200, '2024-06-13 19:49:17.921018', 0, 240870);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240874, 'countyadmin3', 'localhost:8888', '960A4BF3D08CE92209C826A84CF8BFA22D750B017D003EC190547ACB237F3F69', '/contest/county', 200, '2024-06-13 19:49:17.944074', 0, 240873);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240877, 'countyadmin3', 'localhost:8888', '0E32895E4D602AA3F434693F6DAE4E8D91B2A2CE97706E620E9B093B4692B0AC', '/county-dashboard', 200, '2024-06-13 19:49:22.954171', 0, 240874);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240878, 'countyadmin3', 'localhost:8888', 'B51ACF8AF65F589CD6DCE5ED83E3D34C6DD34671BE9D4B5459573F32595B969F', '/contest/county', 200, '2024-06-13 19:49:22.978812', 0, 240877);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240881, 'countyadmin3', 'localhost:8888', 'D10664B44162D209197CC6DEB71D8D56710FB65CDE7BC2727FE874769DEDF0A5', '/county-dashboard', 200, '2024-06-13 19:49:27.989689', 0, 240878);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240882, 'countyadmin3', 'localhost:8888', '46C50725FDA1F2D1811229F018E085ACAE77392A9083FEAF5D20F88DFDED04FA', '/contest/county', 200, '2024-06-13 19:49:28.011734', 0, 240881);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240876, 'countyadmin3', 'localhost:8888', 'BC0FDA2E18BA0ECFAB3425B981CF59BD3B28789575CE9A94ABE1B66121B8C349', '/county-asm-state', 200, '2024-06-13 19:49:22.949661', 0, 240874);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240880, 'countyadmin3', 'localhost:8888', '9E712E7D9DE0DF0C1CDE00805F25EB462492362BE3EE371986E6D8D086256FCA', '/audit-board-asm-state', 200, '2024-06-13 19:49:27.984345', 0, 240878);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240884, 'countyadmin3', 'localhost:8888', '43042B2D13BB0B9EEB59E2C2493016AB49C2EC4C7BC44488E731E0EBE20FABD9', '/audit-board-asm-state', 200, '2024-06-13 19:49:33.017934', 0, 240882);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240883, 'countyadmin3', 'localhost:8888', 'A0AC4F8BBCB21C08A10C4333539207AF55143A313D5D3F7DBBDDEF4CFA88FA2B', '/county-asm-state', 200, '2024-06-13 19:49:33.018045', 0, 240882);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240885, 'countyadmin3', 'localhost:8888', '6B0C8E58852B39626B9CCE34142DBC450393BC388A84A8E9036FC3AC0BE60F0F', '/county-dashboard', 200, '2024-06-13 19:49:33.030562', 0, 240882);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240886, 'countyadmin3', 'localhost:8888', 'BB9AEBA945614474F9D6C6A3FB8139161DFFCFB3472B9A4ED8FC28D2532B49B8', '/contest/county', 200, '2024-06-13 19:49:33.052649', 0, 240885);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240887, 'countyadmin3', 'localhost:8888', 'EFB30C02986259CB5F5309542FD63EB3FEBB642D91C6452BB46DD871E7E8CE0A', '/audit-board-asm-state', 200, '2024-06-13 19:49:38.056016', 0, 240886);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240888, 'countyadmin3', 'localhost:8888', '67B50BB7356E240644D515ED1A29E4F88C99A0857F0B07ED005E9F900E8AFA42', '/county-asm-state', 200, '2024-06-13 19:49:38.056229', 0, 240886);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240889, 'countyadmin3', 'localhost:8888', '0B56901E40FFF73B11380F5EFA399FBA2C31E285149F76D500A00BBA7AA76BBD', '/county-dashboard', 200, '2024-06-13 19:49:38.065166', 0, 240886);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240890, 'countyadmin3', 'localhost:8888', '376321C14DF6D8767C5F66582E6A14B643B53EABAFDE67E6948B380ADA3DADAE', '/contest/county', 200, '2024-06-13 19:49:38.090648', 0, 240889);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240891, 'countyadmin3', 'localhost:8888', '570349DD3D44979CAC00CA741ECFC15EB14F29C05C93764E32A64B5FEE0851FE', '/audit-board-asm-state', 200, '2024-06-13 19:49:44.065894', 0, 240890);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240892, 'countyadmin3', 'localhost:8888', 'D4C835696A7CF89515726E0D73396747735AD50E84594769B2AC171DAFD2950C', '/county-asm-state', 200, '2024-06-13 19:49:44.065725', 0, 240890);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240893, 'countyadmin3', 'localhost:8888', '23975FA57A5FB638EA14ECE011350E1481BB38D2157DEC6E5EF50FC5EC6176CD', '/county-dashboard', 200, '2024-06-13 19:49:44.074183', 0, 240890);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240894, 'countyadmin3', 'localhost:8888', '2393FF749A2F2DDEABF722FEC62E8A2C6358249BEA12DC3AB04D3F4E92D53CEF', '/contest/county', 200, '2024-06-13 19:49:44.10129', 0, 240893);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240895, 'countyadmin3', 'localhost:8888', '37FA93F14A08BF8A6A9D0234B98533E85449731BAC7F4087D2FB02C62BECC571', '/audit-board-asm-state', 200, '2024-06-13 19:49:50.077108', 0, 240894);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240896, 'countyadmin3', 'localhost:8888', '3901728D5BB409A3323F5346EDECD995CEBC515124ACC33EA0846E60804A3D83', '/county-asm-state', 200, '2024-06-13 19:49:50.077213', 0, 240894);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240897, 'countyadmin3', 'localhost:8888', 'C3A395F38FC4B49E863269D7E4DDB7BB5D076FD807FF9942B4258B8E4177690E', '/county-dashboard', 200, '2024-06-13 19:49:50.086419', 0, 240894);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240898, 'countyadmin3', 'localhost:8888', '05645186DB102A1EAD889E2DFC4FEA7743F404B1CA6A4EFEEDC63B6B8D07FEAE', '/contest/county', 200, '2024-06-13 19:49:50.110521', 0, 240897);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240899, 'countyadmin3', 'localhost:8888', 'BCCEA7E8B5F826C47DA6680C906CBF08E12EC34DD69AFE71DAF3D6C8A46BBD71', '/audit-board-asm-state', 200, '2024-06-13 19:49:56.096614', 0, 240898);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240900, 'countyadmin3', 'localhost:8888', '3C3D9CBFE1F764140E0075A708A2BD590990D897ABB00464CBFE00372AA41F46', '/county-asm-state', 200, '2024-06-13 19:49:56.097725', 0, 240898);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240901, 'countyadmin3', 'localhost:8888', '85E0B927EE5C13274FA48454A447E0E0C2D6819BFC1431416403D0D2A92B259B', '/county-dashboard', 200, '2024-06-13 19:49:56.103677', 0, 240898);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240902, 'countyadmin3', 'localhost:8888', '04975F28AE9341CC0CA7EE3AF844D4FA272640BF78752B1B69E73A0C4E8A8FF1', '/contest/county', 200, '2024-06-13 19:49:56.129407', 0, 240901);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240903, 'countyadmin3', 'localhost:8888', 'E6032CC99E933FEB5D852938E0D37C6FF2F034DA0C2F03240484F130522DAE91', '/audit-board-asm-state', 200, '2024-06-13 19:50:02.10964', 0, 240902);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240904, 'countyadmin3', 'localhost:8888', 'DAFA9FDD53C793DA1F7A144CD7D544D8322A34B93D3CAB3CE268275E67090BC8', '/county-asm-state', 200, '2024-06-13 19:50:02.109872', 0, 240902);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240905, 'countyadmin3', 'localhost:8888', 'E1EDDF6143CB294196D82557D5ECDAB09F92C93EE7BDD26E4763E4DD8D2FAC1B', '/county-dashboard', 200, '2024-06-13 19:50:02.117949', 0, 240902);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240906, 'countyadmin3', 'localhost:8888', '030DC5392444E59DFE31AF4875CEEA123FF3CA9BC64E0C80650F9FB3342F7A47', '/contest/county', 200, '2024-06-13 19:50:02.143916', 0, 240905);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240907, 'countyadmin3', 'localhost:8888', '19A9F505D3A844F01FC953D5945F877CA5FB81F99D97E0255F61D6258E388639', '/audit-board-asm-state', 200, '2024-06-13 19:50:08.130527', 0, 240906);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240908, 'countyadmin3', 'localhost:8888', 'E16E58FFD34F85DF1D56D1D38417ACEA8BDCEDBA8EFA99C8447F9217113FB174', '/county-asm-state', 200, '2024-06-13 19:50:08.131266', 0, 240906);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240909, 'countyadmin3', 'localhost:8888', 'B5BA227A74CA2664628961C78A26AEAA00A0A7BFEF5FEEC14490DF872AD1E090', '/county-dashboard', 200, '2024-06-13 19:50:08.137384', 0, 240906);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240910, 'countyadmin3', 'localhost:8888', 'A774DC4F07AF37DB5DD258735775F13E4E172B3ECEF7DA53D7250FA235D493F0', '/contest/county', 200, '2024-06-13 19:50:08.160729', 0, 240909);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240911, 'countyadmin3', 'localhost:8888', 'E4AE829B0656434EAB554F987A52DE9F668E3C42351E119D2D4D3E1EBC3C858C', '/audit-board-asm-state', 200, '2024-06-13 19:50:14.152262', 0, 240910);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240912, 'countyadmin3', 'localhost:8888', 'F6F85B614875B48D3AC1CAC6EACD875C3F966EC79522D542C1C17C612906D426', '/county-asm-state', 200, '2024-06-13 19:50:14.151981', 0, 240910);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240913, 'countyadmin3', 'localhost:8888', 'BE15DF6A526ED05526D9388642FB97DB489AFEC2E8AF23ED80FDF39FE7468DBF', '/county-dashboard', 200, '2024-06-13 19:50:14.161415', 0, 240910);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240914, 'countyadmin3', 'localhost:8888', '327108E95C6CA422ACA45FF6A5EF2D2FABA6CDF7041CB7CF8A06626A4A874C2A', '/contest/county', 200, '2024-06-13 19:50:14.187691', 0, 240913);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240915, 'countyadmin3', 'localhost:8888', 'E3AE067D8F169A7E19EA3FF916D0CA606F53B415872922267517E0A99B7E928D', '/audit-board-asm-state', 200, '2024-06-13 19:50:20.160331', 0, 240914);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240916, 'countyadmin3', 'localhost:8888', 'F221898AC097BCFA2F6F06F1BE0A3C83D99EDD00198DF979822AC558D700D7D1', '/county-asm-state', 200, '2024-06-13 19:50:20.16062', 0, 240914);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240917, 'countyadmin3', 'localhost:8888', '504F63134AE71461A78FA573EE8DA11B095B3D3FD87A2F20800FED713D296E15', '/county-dashboard', 200, '2024-06-13 19:50:20.167511', 0, 240914);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240918, 'countyadmin3', 'localhost:8888', '0DAE423D89DB156B6E3E451BBFDEB0B5F3B1875BAA1D502D48C4648D6535DED0', '/contest/county', 200, '2024-06-13 19:50:20.191654', 0, 240917);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240919, 'countyadmin3', 'localhost:8888', '50A61B5762614C96C6B07A8A87589139FA9AE258A2C985DF3AE7242F9FD829A5', '/audit-board-asm-state', 200, '2024-06-13 19:50:26.183308', 0, 240918);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240920, 'countyadmin3', 'localhost:8888', '8D18A7D766A96EDB15767982C61C5CFDB1047C3A0781E9688F9BE5C2E7CD230E', '/county-asm-state', 200, '2024-06-13 19:50:26.183713', 0, 240918);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240921, 'countyadmin3', 'localhost:8888', '93C72B7A65769EE49FB807705BC9253A576A2A8ACEDE781D65358B32CA0BC0BF', '/county-dashboard', 200, '2024-06-13 19:50:26.191522', 0, 240918);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240922, 'countyadmin3', 'localhost:8888', 'B1BFAAA2CB28BAF48D4BA3DE6EFD5EC20F2A7BABFE3F6329B8A24C5DF6610653', '/contest/county', 200, '2024-06-13 19:50:26.216189', 0, 240921);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240923, 'countyadmin3', 'localhost:8888', '445974B60253CA3D161BDCE110305A3B1D8469D06932177BB4BFD2DE0F19DF82', '/audit-board-asm-state', 200, '2024-06-13 19:50:32.193519', 0, 240922);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240924, 'countyadmin3', 'localhost:8888', '32ABFC2A30A73FDCD4B72D6352B5808371BA4A6DD1AEA3A9CBCAF4C009619EF6', '/county-asm-state', 200, '2024-06-13 19:50:32.194873', 0, 240922);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240925, 'countyadmin3', 'localhost:8888', 'D2E276A158F9F6D3E7CE73491F59DEE2BF891297FD12100A1B6B0AFBFD5402E8', '/county-dashboard', 200, '2024-06-13 19:50:32.201871', 0, 240922);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240926, 'countyadmin3', 'localhost:8888', 'C7EF31432A58E7D7E5BB5411E8474A4A5626D6CF9C1954F567363C026B9AED90', '/contest/county', 200, '2024-06-13 19:50:32.225246', 0, 240925);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240929, 'countyadmin3', 'localhost:8888', 'FCA39B5F262BFBFD15EBF04949CCCC61214A4AA6F246616851A97CA8D341F780', '/county-dashboard', 200, '2024-06-13 19:50:38.221652', 0, 240926);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240930, 'countyadmin3', 'localhost:8888', '4543F152641408302565CDE0146EBC89350EB5F09726658EB51ED2922BF649C2', '/contest/county', 200, '2024-06-13 19:50:38.248283', 0, 240929);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240927, 'countyadmin3', 'localhost:8888', '68A4E9B5FB59652290120E98D6407B4154181419F458C72920D7B1948F0FFA37', '/audit-board-asm-state', 200, '2024-06-13 19:50:38.214476', 0, 240926);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240928, 'countyadmin3', 'localhost:8888', '53E16A76695769459E03D285C66C630C34BBBDDFF7BD08972CF846E8BFE8F20A', '/county-asm-state', 200, '2024-06-13 19:50:38.215856', 0, 240926);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240931, 'countyadmin3', 'localhost:8888', 'DAFC0C28A892F39A551D1072382AF2CF5299EF1F796E5F0BAA71D125075158D9', '/audit-board-asm-state', 200, '2024-06-13 19:50:44.225183', 0, 240930);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240932, 'countyadmin3', 'localhost:8888', '033BF54B38E40B0DECEBFC3613D843D34DDF797A173420A1F77E3656CEECAE11', '/county-asm-state', 200, '2024-06-13 19:50:44.225489', 0, 240930);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240933, 'countyadmin3', 'localhost:8888', '3ED3C8464790857DE18F6E4204C793804F7A8B4ECABABDA915C355DD00E4380C', '/county-dashboard', 200, '2024-06-13 19:50:44.232557', 0, 240930);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240934, 'countyadmin3', 'localhost:8888', 'EFA6D4BE8FE73CC7D656B1AC9B9CB3D43F534AA257928F3DDFE82BCF512EC95B', '/contest/county', 200, '2024-06-13 19:50:44.257597', 0, 240933);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240935, 'countyadmin3', 'localhost:8888', 'C764BF912773AD5F79C93925BE43BF42432C75D10EEF1806D096E208C671D5C6', '/audit-board-asm-state', 200, '2024-06-13 19:50:49.264227', 0, 240934);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240936, 'countyadmin3', 'localhost:8888', 'FD1B07C028FDB611B37C9C0592402067D97222139FE313542E196C39223807FB', '/county-asm-state', 200, '2024-06-13 19:50:49.266098', 0, 240934);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240937, 'countyadmin3', 'localhost:8888', '7F7FAA38E35C968ED9B28101BBA82AB99830503B5B48C8B9C0BF498C393EEC8C', '/county-dashboard', 200, '2024-06-13 19:50:49.273197', 0, 240934);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240938, 'countyadmin3', 'localhost:8888', '201245A6AFDF6D2B8D7F0187D2C4E31C4D995CDBE5C87D8BA9322799F944AD3F', '/contest/county', 200, '2024-06-13 19:50:49.300394', 0, 240937);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240939, 'countyadmin3', 'localhost:8888', '41B43B4420A22BCFAAFE929482B74399955C4A5BEBBE2706DD17B3C4D2ABE831', '/audit-board-asm-state', 200, '2024-06-13 19:50:54.304838', 0, 240938);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240940, 'countyadmin3', 'localhost:8888', '07627453D7D077E8C175C445BD3C5DA5108C87A015998FF9226940D60166B3B6', '/county-asm-state', 200, '2024-06-13 19:50:54.305239', 0, 240938);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240941, 'countyadmin3', 'localhost:8888', '18105DF9AE92A4C5D9AC6DECE2F2FA67DC96C969F22BB37C0B2DE1CBF6762367', '/county-dashboard', 200, '2024-06-13 19:50:54.310721', 0, 240938);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240942, 'countyadmin3', 'localhost:8888', '8580A48BC04703D2680F81BC11599A5981753ED3440D1798A45835BBCE9E6606', '/contest/county', 200, '2024-06-13 19:50:54.335247', 0, 240941);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240943, 'countyadmin3', 'localhost:8888', 'F43CF18A852ED2B8B871A4A4D84EB7867ADC92667713EE5C0F34F9028E8F0B72', '/audit-board-asm-state', 200, '2024-06-13 19:51:00.321054', 0, 240942);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240944, 'countyadmin3', 'localhost:8888', '4B76F96D4EA8AF0762BC2F4229A5AA45CE31F8DACBB74A5978C2E87B9BAB113A', '/county-asm-state', 200, '2024-06-13 19:51:00.321793', 0, 240942);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240945, 'countyadmin3', 'localhost:8888', '10F3303E4B998D511D5B5922AE587348A2AFA5F0E4F5BDDFF92610102F06275E', '/county-dashboard', 200, '2024-06-13 19:51:00.328545', 0, 240942);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240946, 'countyadmin3', 'localhost:8888', '7711AE45B69D3FFF50950ED96EE0EDCA9701C6F4A6341825DF660C19C3506C76', '/contest/county', 200, '2024-06-13 19:51:00.35358', 0, 240945);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240947, 'countyadmin3', 'localhost:8888', 'B8EF68800D08700206E849382CA12CDEAFD9E411234A0ED46216CA3B2FBDD6F8', '/audit-board-asm-state', 200, '2024-06-13 19:51:06.331358', 0, 240946);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240948, 'countyadmin3', 'localhost:8888', 'BD590C63AE92913C270B038B3D73CE2A34D08E7F6E5332BA71D7C832E3FF2355', '/county-asm-state', 200, '2024-06-13 19:51:06.332307', 0, 240946);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240949, 'countyadmin3', 'localhost:8888', '7B28CA358AA0D7F3B4F93550A5E4CDD622D5CF9EE558EAD6E554DC7DBF3CC37E', '/county-dashboard', 200, '2024-06-13 19:51:06.339666', 0, 240946);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240950, 'countyadmin3', 'localhost:8888', '5F4DE0F8A45018A92F24D88BFA3460444D4EB866B43D83F3B474C6350645CD29', '/contest/county', 200, '2024-06-13 19:51:06.36378', 0, 240949);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240951, 'countyadmin3', 'localhost:8888', '3DE33243DD2989DFA95559C2665A27BE6884C78C686E21AE92A034CE52A2390E', '/audit-board-asm-state', 200, '2024-06-13 19:51:12.338762', 0, 240950);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240952, 'countyadmin3', 'localhost:8888', 'B49BEBECE6E9ECF3ADDE2CFD974753CC55E5C0D10F5CD1EF7F4830E81DDBCDB0', '/county-asm-state', 200, '2024-06-13 19:51:12.340112', 0, 240950);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240953, 'countyadmin3', 'localhost:8888', '3625392591629D9FD5134CC47B3ECE3D27F4FBF991A215814B2DE0BCD96210AB', '/county-dashboard', 200, '2024-06-13 19:51:12.347219', 0, 240950);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240954, 'countyadmin3', 'localhost:8888', 'F75EC8C42A05D2AEA9BB38C702FD66391A55E675F600CFE381367652373FD2C5', '/contest/county', 200, '2024-06-13 19:51:12.369056', 0, 240953);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240955, 'countyadmin3', 'localhost:8888', 'FDA9FFD4B7E7A025051ACAE7680CEC7B7ED4170D50983DFDB27E074833EDBD89', '/audit-board-asm-state', 200, '2024-06-13 19:51:18.346933', 0, 240954);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240956, 'countyadmin3', 'localhost:8888', '0E7AF5961B18BF53DB2C96B0040DA977995DCDB4511DE4FD4A41A1238F00A4DA', '/county-asm-state', 200, '2024-06-13 19:51:18.347237', 0, 240954);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240957, 'countyadmin3', 'localhost:8888', 'B6FC9F3B2D314232DA96CD97B999FFB73DAD3C37310F2B2313D02C55CEF5BF57', '/county-dashboard', 200, '2024-06-13 19:51:18.354379', 0, 240954);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240958, 'countyadmin3', 'localhost:8888', '6194BC773413B8972506B01FCBAACB78694B6C4DF50ED7C6F68DCE45AF64CC3E', '/contest/county', 200, '2024-06-13 19:51:18.375473', 0, 240957);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240959, 'countyadmin3', 'localhost:8888', 'E8EF3702E8C711E776AFCCE4F6E15A8CCDC521A8C1EFB83B77BBCD2E6E4B3F8D', '/audit-board-asm-state', 200, '2024-06-13 19:51:24.356448', 0, 240958);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240960, 'countyadmin3', 'localhost:8888', '1A7C72753D756E7D6D956D50361810BD7C4E21552EC6CAE04A8DCBBB82EFABD7', '/county-asm-state', 200, '2024-06-13 19:51:24.35721', 0, 240958);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240961, 'countyadmin3', 'localhost:8888', '02C4F2EFE31EC3FC05BFC93033E1CD90BFD5C1546D13CE37B2B141DED2C5B035', '/county-dashboard', 200, '2024-06-13 19:51:24.364244', 0, 240958);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240962, 'countyadmin3', 'localhost:8888', '3735ABD35C8AC9BB6C85F6482BDCB1DD9FDB4D21EFB4D6C3357C3C8D7497FE70', '/contest/county', 200, '2024-06-13 19:51:24.390278', 0, 240961);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240963, 'countyadmin3', 'localhost:8888', 'EFD7883F71515BD8E73C2BF4B4B2B46112481BB72864079D10C4D0FC93F49E23', '/audit-board-asm-state', 200, '2024-06-13 19:51:30.38584', 0, 240962);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240964, 'countyadmin3', 'localhost:8888', '5F043671B6327B940FAF07011824DEEAB769F13C6EB814B88867137965B8AD8E', '/county-asm-state', 200, '2024-06-13 19:51:30.386796', 0, 240962);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240965, 'countyadmin3', 'localhost:8888', '5700F406680AB5706B2C29DEF8130507F9BD17DCB87B20C8F7E39488F6739181', '/county-dashboard', 200, '2024-06-13 19:51:30.393572', 0, 240962);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240966, 'countyadmin3', 'localhost:8888', '677295AE6A1AB2AF79556F8CEBF589C5BBFC60305BAAED414A417D600DD1AE77', '/contest/county', 200, '2024-06-13 19:51:30.421295', 0, 240965);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240967, 'countyadmin3', 'localhost:8888', '81098080288FE2C071EC8A54762A30D3CFF82F59EF30011FAC6625D2573EC29A', '/audit-board-asm-state', 200, '2024-06-13 19:51:35.425104', 0, 240966);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240968, 'countyadmin3', 'localhost:8888', 'AA9F3E832981CB4CCFBC5B7F23B27DE6752EC3B183FFBB9FDDA802A61DBAAD66', '/county-asm-state', 200, '2024-06-13 19:51:35.426033', 0, 240966);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240969, 'countyadmin3', 'localhost:8888', '4D0385D031E01DE58AABCCCEC26C64C619A65C261A3244C80B1DCD64C9433A98', '/county-dashboard', 200, '2024-06-13 19:51:35.432177', 0, 240966);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240970, 'countyadmin3', 'localhost:8888', 'BB3FD963B0442AD225D7540A0AAF132D17F807B7EDBCC7801CA2D86AD5E49DF5', '/contest/county', 200, '2024-06-13 19:51:35.456254', 0, 240969);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240971, 'countyadmin3', 'localhost:8888', '46F55792BE0D5CB428B460F81C08C88DABF3613FCDA8A0B5EA4CDAFA6C07B559', '/audit-board-asm-state', 200, '2024-06-13 19:51:41.446593', 0, 240970);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240972, 'countyadmin3', 'localhost:8888', '1EE142DEBAB33D5741CA4D651468DF1C4DE6CF18487A8B8E9FC850FDEC979E9D', '/county-asm-state', 200, '2024-06-13 19:51:41.446974', 0, 240970);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240973, 'countyadmin3', 'localhost:8888', '2134A83B661C2677EBF38903C4E1416DBCA134A833C6933983C4C2C23F24F749', '/county-dashboard', 200, '2024-06-13 19:51:41.452468', 0, 240970);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240974, 'countyadmin3', 'localhost:8888', 'A878E67707380001F92736B9263E26543FA43D1B21E0871D109568110B96088B', '/contest/county', 200, '2024-06-13 19:51:41.476516', 0, 240973);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240975, 'countyadmin3', 'localhost:8888', 'F82D5DAD4F504491726ADEA7BBF1775D64029294023CE3B8B09B208E92BEA376', '/audit-board-asm-state', 200, '2024-06-13 19:51:47.456189', 0, 240974);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240980, 'countyadmin3', 'localhost:8888', '44AB8D7740D91127EA1069F080AB2980702014B736C791F8023B571775F57A7E', '/county-asm-state', 200, '2024-06-13 19:51:52.497097', 0, 240978);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240983, 'countyadmin3', 'localhost:8888', '748F768C13B63A7929BB54986E16B9189192E293364F4D1FE7F4C2E42562EE81', '/audit-board-asm-state', 200, '2024-06-13 19:51:58.506722', 0, 240982);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241045, 'countyadmin3', 'localhost:8888', 'DA56897D8460A05F77E0B32724546D058DCA6255A914870FE843B4857D2279BC', '/import-cvr-export', 200, '2024-06-13 19:53:11.793119', 0, 241042);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241046, 'countyadmin3', 'localhost:8888', '3860F4369FDD9E15BD4D8011B8A1407B126B40632A22C69CA0A3756408C1CD97', '/contest/county', 200, '2024-06-13 19:53:11.827208', 0, 241045);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241048, 'countyadmin3', 'localhost:8888', '081057996B800E0F87408BEA8B45C5763DC473946678CC5AEFFC8F98D3D02C97', '/county-asm-state', 200, '2024-06-13 19:53:11.837266', 0, 241046);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241067, 'countyadmin3', 'localhost:8888', 'DDF24DDE9DF264BBC3DC2C9546CED13475EE1BE9671948784A9ED52F0198DAB3', '/audit-board-asm-state', 200, '2024-06-13 19:53:15.465386', 0, 241050);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240976, 'countyadmin3', 'localhost:8888', 'B344F3F7F50E50F6FF8CF45D02511E661CBCC7FD879904332D353F90E28F307A', '/county-asm-state', 200, '2024-06-13 19:51:47.457326', 0, 240974);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240979, 'countyadmin3', 'localhost:8888', '70DDC3C0CE0D61E00729869980DA4238C74C8F4DABDE402C8B33DB8C9AF26E9A', '/audit-board-asm-state', 200, '2024-06-13 19:51:52.496423', 0, 240978);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240984, 'countyadmin3', 'localhost:8888', '53AE813AB7F96806920D8F8CE18A3AED7E8C553D4FA092C6BE7470B6624D4D61', '/county-asm-state', 200, '2024-06-13 19:51:58.507233', 0, 240982);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240988, 'countyadmin3', 'localhost:8888', '51A4407FD0B2993247EFE7352D69F1D844D8DD2B8141D53C1F3720A3A2845075', '/county-asm-state', 200, '2024-06-13 19:52:04.518495', 0, 240986);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240991, 'countyadmin3', 'localhost:8888', 'DDB4EBB564422E4F0AC3CC11892515A19C67BA05C0B1D2E0B90607B24D20C734', '/audit-board-asm-state', 200, '2024-06-13 19:52:10.557155', 0, 240990);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240996, 'countyadmin3', 'localhost:8888', '6D720F2EEED6B450324C651AC75104DE565E9A0F048C6DC3B2947096A4FA8AD2', '/county-asm-state', 200, '2024-06-13 19:52:16.565474', 0, 240994);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240999, 'countyadmin3', 'localhost:8888', 'EBB9EEBE6BC71824E347ACD435BDADB8AA209451CD0C6F851DE275C49696886A', '/audit-board-asm-state', 200, '2024-06-13 19:52:22.573874', 0, 240998);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241004, 'countyadmin3', 'localhost:8888', '1654C24C04C8CC5ACD0B99FA3575AB946C7C2F24B7599F602DEBCB565EA57602', '/county-asm-state', 200, '2024-06-13 19:52:28.591676', 0, 241002);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241007, 'countyadmin3', 'localhost:8888', '149758302F269627F1109CAD5CA60ED9151A9A40276605C8FC5226B299442506', '/audit-board-asm-state', 200, '2024-06-13 19:52:34.59923', 0, 241006);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241011, 'countyadmin3', 'localhost:8888', '3A1406F7EC99498B75B52E9FD00B4253721ECD656702F9C1A6F26CAC78AAB58A', '/audit-board-asm-state', 200, '2024-06-13 19:52:39.631894', 0, 241010);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241017, 'countyadmin3', 'localhost:8888', 'CD32769F3335FB478C309F51A69E6A6D5EEA15D15F6F9FC50D7DCE9EC9F66772', '/county-asm-state', 200, '2024-06-13 19:52:44.667475', 0, 241015);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241020, 'countyadmin3', 'localhost:8888', '3CB5C4D96E8D23BB68BC756E285A8A77CB90B71E589AABAE2179D7E9FB37DA37', '/audit-board-asm-state', 200, '2024-06-13 19:52:49.720058', 0, 241019);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241025, 'countyadmin3', 'localhost:8888', 'D63125CDAD1FAA61A5683C58EB30DB60E9E68B2B53EDEC5C74202C3E03D03A2E', '/county-asm-state', 200, '2024-06-13 19:52:54.754437', 0, 241023);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241028, 'countyadmin3', 'localhost:8888', '3540A0882C61B2B4B54DEBB2C86841220E497AE26FAB5EB42926930C698EB9C8', '/audit-board-asm-state', 200, '2024-06-13 19:52:59.792487', 0, 241027);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241033, 'countyadmin3', 'localhost:8888', '7A7692FDDF675D671BAD6E6919DDAB1689F558D929794D82F4DCAD759C7F7770', '/county-asm-state', 200, '2024-06-13 19:53:05.345074', 0, 241031);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241036, 'countyadmin3', 'localhost:8888', '330DF9B4F0E469AD08851A3DBD7AAE92F0D0E27A369058F3001D7BA4C0D22E0E', '/audit-board-asm-state', 200, '2024-06-13 19:53:10.41013', 0, 241035);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241043, 'countyadmin3', 'localhost:8888', 'DBC46E4799CBB10718CCB4497EA4D114AEC057476CEECB73520AECFFD856AA8F', '/county-asm-state', 200, '2024-06-13 19:53:11.778403', 0, 241041);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241047, 'countyadmin3', 'localhost:8888', 'F75B285CE89A6CE6443DDC404D2233FD956835346C3E91E15813E448F830E7DF', '/audit-board-asm-state', 200, '2024-06-13 19:53:11.831734', 0, 241045);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241068, 'countyadmin3', 'localhost:8888', '2ECCF0F39A81F8320D48664D898FE24993CA1FB92C972EBD5117E2526BDAFC97', '/county-asm-state', 200, '2024-06-13 19:53:15.466517', 0, 241050);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240977, 'countyadmin3', 'localhost:8888', '104D931DE2696878E87215AD237A7655AD564B35FB5459C7197E5E7F2B8B4B0B', '/county-dashboard', 200, '2024-06-13 19:51:47.463748', 0, 240974);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240978, 'countyadmin3', 'localhost:8888', '5513BD748AC242E40263E80453C4DECB0AA561608D2053714A05ED0E83ACEC7E', '/contest/county', 200, '2024-06-13 19:51:47.490023', 0, 240977);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240981, 'countyadmin3', 'localhost:8888', '209E4337E16BF1E475F666C35439932A3709D81B4A9B02C16C3729BFEFF12DD5', '/county-dashboard', 200, '2024-06-13 19:51:52.50297', 0, 240978);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240982, 'countyadmin3', 'localhost:8888', 'A929ED963FA8498E073BC5F7BC3886C6F6F0B754C911971DB874F28DA4282C1A', '/contest/county', 200, '2024-06-13 19:51:52.537829', 0, 240981);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240985, 'countyadmin3', 'localhost:8888', 'F9BE642C0117C99B59F11F774AF3B7B435B795B4443606BD9B401E03DB2A167A', '/county-dashboard', 200, '2024-06-13 19:51:58.514213', 0, 240982);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240986, 'countyadmin3', 'localhost:8888', 'C62890A48487A1396F897B159C5A52224B1F064DE751B16F29FEE44D9F32DA2F', '/contest/county', 200, '2024-06-13 19:51:58.537927', 0, 240985);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240987, 'countyadmin3', 'localhost:8888', '818820DD879F6417FDB435A5CE2FB2E45EAA2BC0E83D4D6A805C26EB7D6AD56F', '/audit-board-asm-state', 200, '2024-06-13 19:52:04.517471', 0, 240986);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240989, 'countyadmin3', 'localhost:8888', '9EB5A4627EBBEFBFFBB5445D4DBB1A66EA9261865E13528A1EE13853BBB2432A', '/county-dashboard', 200, '2024-06-13 19:52:04.529321', 0, 240986);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240990, 'countyadmin3', 'localhost:8888', '281F28FB86032312AA78C8D863EDE7B7E55C578BBBE2E00D9A2BD362EC3FCD61', '/contest/county', 200, '2024-06-13 19:52:04.555539', 0, 240989);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240992, 'countyadmin3', 'localhost:8888', '30E68DA361CC1F5A74C0922ED9EB7E339F4362EAA2053331AB70AA7B2E09230C', '/county-asm-state', 200, '2024-06-13 19:52:10.558336', 0, 240990);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240993, 'countyadmin3', 'localhost:8888', '6952C60DE10ACBB10187DCE47F009321D445564FC48FDE0F35329035E11462D4', '/county-dashboard', 200, '2024-06-13 19:52:10.568298', 0, 240990);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240994, 'countyadmin3', 'localhost:8888', '199EBBAD1DE153C30339BFE5269C3F141EAB8B1E1F9F0229B0ADD566C5D525FE', '/contest/county', 200, '2024-06-13 19:52:10.597399', 0, 240993);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240995, 'countyadmin3', 'localhost:8888', '224CC2AB453C768422783E43C098EDC7B205CBBE2B20FCB93F9A42EC8386D4E5', '/audit-board-asm-state', 200, '2024-06-13 19:52:16.563995', 0, 240994);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240997, 'countyadmin3', 'localhost:8888', 'E2C8C3236420033401F31C107827188CF6C13DEEF5391C3B1E198191AA758125', '/county-dashboard', 200, '2024-06-13 19:52:16.574363', 0, 240994);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240998, 'countyadmin3', 'localhost:8888', 'A8FD6E794297886826593462164B2CAE42030DCCCD1EF75C528D6EEA383A092C', '/contest/county', 200, '2024-06-13 19:52:16.594896', 0, 240997);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241000, 'countyadmin3', 'localhost:8888', 'F6BBE672E725A82D8AC876AF24AD40BF525318EDA59217E72CE7BDD01A59A4CB', '/county-asm-state', 200, '2024-06-13 19:52:22.575083', 0, 240998);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241001, 'countyadmin3', 'localhost:8888', '596888C10727E78EF56E4CAE0461AA6748EB33526272E8F213B572B12CD51DFA', '/county-dashboard', 200, '2024-06-13 19:52:22.584333', 0, 240998);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241002, 'countyadmin3', 'localhost:8888', '2E176A095533BC83A9C2212201AE905479932AF04BCC87679A1289A49B8D88CC', '/contest/county', 200, '2024-06-13 19:52:22.611366', 0, 241001);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241003, 'countyadmin3', 'localhost:8888', '61C9172F427872CDD9F136FFE090A736691E56B35366B47F2EFB492544996B6C', '/audit-board-asm-state', 200, '2024-06-13 19:52:28.591225', 0, 241002);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241005, 'countyadmin3', 'localhost:8888', '68C3E8092AF9449EFE093A81FCCD72B241F43A8E22CCC6F5DA10EEA15811CC73', '/county-dashboard', 200, '2024-06-13 19:52:28.600336', 0, 241002);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241006, 'countyadmin3', 'localhost:8888', 'A2253BDB84B1DF5FFE8ADDBC6EFE93C381CBE0860F5B0E856585DC196447E18D', '/contest/county', 200, '2024-06-13 19:52:28.624954', 0, 241005);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241008, 'countyadmin3', 'localhost:8888', '41BE51F8DCCDE31F55322207C4F730681138AE423AEEF58A638B5CE0E6B3ECA8', '/county-asm-state', 200, '2024-06-13 19:52:34.599694', 0, 241006);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241009, 'countyadmin3', 'localhost:8888', '2EEF696BDB2125BE945E02EFC18899CA19DB8F03BE342F96F9A09B65E63E1A84', '/county-dashboard', 200, '2024-06-13 19:52:34.606203', 0, 241006);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241010, 'countyadmin3', 'localhost:8888', '9D9E16E93B2DE970EE75E9AB682E03E5637A3768B5E28C6F949B04FF87C30824', '/contest/county', 200, '2024-06-13 19:52:34.630906', 0, 241009);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241012, 'countyadmin3', 'localhost:8888', 'E7137358C23B53D1817EF5263D57AB9F2849F0FCFBB9D813B92B06CEC24C048B', '/county-asm-state', 200, '2024-06-13 19:52:39.63344', 0, 241010);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241013, 'countyadmin3', 'localhost:8888', '1541C28D0ED878F6ED61921334ECC3EB8ED2E7B0D8AAD8C89A1A2095D1C0A606', '/county-dashboard', 200, '2024-06-13 19:52:39.637695', 0, 241010);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241014, 'countyadmin3', 'localhost:8888', '2B8BECFBF7F515D5D854456F61CE0C9D7F58C98C39858A5FD436F8C49E3B63BB', '/contest/county', 200, '2024-06-13 19:52:39.658843', 0, 241013);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241015, 'countyadmin3', 'localhost:8888', '48A1C7B2AA54094A88495D9B8DF2AB433578233B743C9D51D593FEBA6E26BAC0', '/delete-file', 200, '2024-06-13 19:52:40.533098', 0, 241014);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241016, 'countyadmin3', 'localhost:8888', '9452B8DF548D3CAD77E5ECB2A2AA10EB446AE9AA2750B6CB0B2EEF50A8035CD3', '/audit-board-asm-state', 200, '2024-06-13 19:52:44.66511', 0, 241015);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241018, 'countyadmin3', 'localhost:8888', '6C4611B53752B12406B378BD01721637DCC22F2489A7951B02F5E84AAF7EA373', '/county-dashboard', 200, '2024-06-13 19:52:44.676124', 0, 241015);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241019, 'countyadmin3', 'localhost:8888', '931ADDAABC7015A0BA38C8F4DED0183F98C7E074664B69F03DBD45A6021ABBA4', '/contest/county', 200, '2024-06-13 19:52:44.720128', 0, 241018);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241021, 'countyadmin3', 'localhost:8888', '950AE50697FB33541F684B40D5CCE680CC6E1665416B5D4F9AFC40960C391A64', '/county-asm-state', 200, '2024-06-13 19:52:49.721172', 0, 241019);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241022, 'countyadmin3', 'localhost:8888', '92E1D3B802F3E6CA61626E17395918D846E20900889342B26405EFA81BB0F47B', '/county-dashboard', 200, '2024-06-13 19:52:49.725629', 0, 241019);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241023, 'countyadmin3', 'localhost:8888', '4B9FDFF34F91881F2B6B3BAA5ED79C789843F5223A16828364F71D21D61BBDC9', '/contest/county', 200, '2024-06-13 19:52:49.747782', 0, 241022);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241024, 'countyadmin3', 'localhost:8888', '373C5AA075B25046D0D49BD5595C99F276E5BE64468231FA6A98A7110B96F415', '/audit-board-asm-state', 200, '2024-06-13 19:52:54.753401', 0, 241023);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241026, 'countyadmin3', 'localhost:8888', '7F2751D86CDF430284221471FF911545F126626E793C789130B8150600FCF4A7', '/county-dashboard', 200, '2024-06-13 19:52:54.759878', 0, 241023);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241027, 'countyadmin3', 'localhost:8888', 'B8CA90A7CF06ECC8422D1EA0B9B05ECEB01905D6354DBD14459688F9AF238335', '/contest/county', 200, '2024-06-13 19:52:54.784988', 0, 241026);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241029, 'countyadmin3', 'localhost:8888', '894A6FEE00DA7FAD907D5E124E4E4AD29CD8C4CB9B619631F84A482EF0423D7D', '/county-asm-state', 200, '2024-06-13 19:52:59.793896', 0, 241027);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241030, 'countyadmin3', 'localhost:8888', 'D5BEE76670A5630532B26D6F85C8474BAAA70BFDA3320E6ACA3DCBD1315A7E1A', '/county-dashboard', 200, '2024-06-13 19:52:59.794795', 0, 241027);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241031, 'countyadmin3', 'localhost:8888', 'C3219CBE44EE00869C0A53B31069A1C2F8903D27128BA28B0783CE2034F7E2E1', '/contest/county', 200, '2024-06-13 19:52:59.847372', 0, 241030);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241032, 'countyadmin3', 'localhost:8888', '3EA72019242365D6D66083E40F471BFEBD553D44237A50CDBB1D5CC99598C999', '/audit-board-asm-state', 200, '2024-06-13 19:53:05.34334', 0, 241031);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241034, 'countyadmin3', 'localhost:8888', '2C5DB02E2788C3F120D5BC178D3613F7192C89CFD7AED356649519F2EB243B71', '/county-dashboard', 200, '2024-06-13 19:53:05.350166', 0, 241031);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241035, 'countyadmin3', 'localhost:8888', '95974CDDA99F84D0198737370D68B7D019DAB8C9676883CED6DB16B4060136B3', '/contest/county', 200, '2024-06-13 19:53:05.402986', 0, 241034);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241037, 'countyadmin3', 'localhost:8888', '792FB8979A10C32495651EBEC7A60B6975972398942C3042F91589FEB1AF337F', '/county-asm-state', 200, '2024-06-13 19:53:10.411262', 0, 241035);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241038, 'countyadmin3', 'localhost:8888', 'E3F0D28AF7DDF922E9BC0A3A579B48BD8855306E4D851FD8FCC6C3B8E5A66A81', '/county-dashboard', 200, '2024-06-13 19:53:10.416704', 0, 241035);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241039, 'countyadmin3', 'localhost:8888', 'B2D6502865F9D3ED18AC5432A89B6405B6B81F2229BD8F86A60405D9D727B5AC', '/contest/county', 200, '2024-06-13 19:53:10.459904', 0, 241038);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241041, 'countyadmin3', 'localhost:8888', 'FD9A846282CEF098C4FAB3366D41C1E7795A8A42DAA136A8466C2A45BAC6F55E', '/upload-file', 200, '2024-06-13 19:53:11.746695', 0, 241039);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241044, 'countyadmin3', 'localhost:8888', '9F99F940046276AD0CF42E8E87CC095EAA92F0E1DF65C5BD712445532717999A', '/county-dashboard', 200, '2024-06-13 19:53:11.788193', 0, 241041);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241049, 'countyadmin3', 'localhost:8888', '05B33E7BF476CCC926DEB0681DD7B5379F4BC2A9C0F135BE1F9F7A683DDD80B9', '/county-dashboard', 200, '2024-06-13 19:53:11.840101', 0, 241045);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241050, 'countyadmin3', 'localhost:8888', 'FA168DD91781E731F56E7E4596B3ABB5295528F94697203C3C89CEA73F18B0AB', '/contest/county', 200, '2024-06-13 19:53:11.865298', 0, 241049);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241069, 'countyadmin3', 'localhost:8888', '55B3BB76801D5A23B14A7507BD14CDFCF4AF516DC0C86FF8D55273ABE5BBFC39', '/county-dashboard', 200, '2024-06-13 19:53:15.471645', 0, 241050);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241070, 'countyadmin3', 'localhost:8888', 'F78B577664FE0CA2BC9D99D57DA17DC1594431F03281E253B0AA847A909F6E40', '/contest/county', 200, '2024-06-13 19:53:15.536338', 0, 241069);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241042, 'countyadmin3', 'localhost:8888', 'C05171A2C9EDFB223B775EA9DE562185DE5B81BFD63B94E25FEC7CA479A55FC5', '/audit-board-asm-state', 200, '2024-06-13 19:53:11.77665', 0, 241041);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241071, 'countyadmin3', 'localhost:8888', '89D66406ED12401DD64381D102EC096BE2B5DB86F9B845C4EB108F02510A58A3', '/county-asm-state', 200, '2024-06-13 19:53:20.541503', 0, 241070);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241072, 'countyadmin3', 'localhost:8888', 'E92B2A54A0C53FDF0C3E60FFBB36877B3777AFE199A9F76F075D3FE31387DD9F', '/audit-board-asm-state', 200, '2024-06-13 19:53:20.541313', 0, 241070);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241073, 'countyadmin3', 'localhost:8888', '9A86C113F7D4F85F2956ECD2C0613DF94EE658B8A94337E4B1E946CB0617BEBE', '/county-dashboard', 200, '2024-06-13 19:53:20.556334', 0, 241070);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241074, 'countyadmin3', 'localhost:8888', 'DA7DD538DF541C687E8EC5340E77B7E3A4EC56D2129635D712AD76FF62C5F1A5', '/contest/county', 200, '2024-06-13 19:53:20.585574', 0, 241073);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241075, 'countyadmin3', 'localhost:8888', '1C241594D1B7A87D97F0C8F2C47B58EAEB3200F1B1D7794BE7C512156630C571', '/audit-board-asm-state', 200, '2024-06-13 19:53:25.590284', 0, 241074);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241076, 'countyadmin3', 'localhost:8888', 'FBD99A6D54D8678C49B83E76864495DE025E00C3551586C8F4D354932D537FDB', '/county-asm-state', 200, '2024-06-13 19:53:25.59198', 0, 241074);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241077, 'countyadmin3', 'localhost:8888', 'D8A8FCC1CFDF12E30CCB571AE1782B814488BCE5F38024F9A2B15C3C278F38AA', '/county-dashboard', 200, '2024-06-13 19:53:25.599822', 0, 241074);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241078, 'countyadmin3', 'localhost:8888', '840189E297FB84C0F67D4E1C32E692A8AD7181BED3FA7F1FA84C50C7DC1A3FC1', '/contest/county', 200, '2024-06-13 19:53:25.634291', 0, 241077);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241079, 'countyadmin3', 'localhost:8888', '0167BBF17B4FD97109F887CB085113B357826199BFE6810FB23BAC5E69FBF777', '/audit-board-asm-state', 200, '2024-06-13 19:53:30.634215', 0, 241078);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241080, 'countyadmin3', 'localhost:8888', '4716041D6110AD14B083FD8AFDCEF7FF1591A23AB053D4D841840EEFDBDA7FB7', '/county-asm-state', 200, '2024-06-13 19:53:30.635241', 0, 241078);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241081, 'countyadmin3', 'localhost:8888', '7FB63FC829F5F5A7BA1ED4AEA171070286D4B7039384F15302C49E4B8F1E766F', '/county-dashboard', 200, '2024-06-13 19:53:30.644158', 0, 241078);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241082, 'countyadmin3', 'localhost:8888', 'D65D149E2D7B7D3334B1B4031300ADE029A30BBD1C53950DED3BD8D6F524F399', '/contest/county', 200, '2024-06-13 19:53:30.670281', 0, 241081);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241083, 'countyadmin3', 'localhost:8888', '672DE583EB554A261F754CF0C77835899E61741A482C55F9621B7AB00259F7A5', '/audit-board-asm-state', 200, '2024-06-13 19:53:35.674138', 0, 241082);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241084, 'countyadmin3', 'localhost:8888', 'C5D2AA2D4630825659664208A5BE5646186FB03A152C74CE84ECE7313B584AB5', '/county-asm-state', 200, '2024-06-13 19:53:35.674869', 0, 241082);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241085, 'countyadmin3', 'localhost:8888', '5E025DD690D3D0CC352AC662A03364F520DA6F821E9461C480141B3688105E06', '/county-dashboard', 200, '2024-06-13 19:53:35.682948', 0, 241082);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241086, 'countyadmin3', 'localhost:8888', '0755504779FDB0237C03E9CFF33DBF7BD7572D48EB3B76E2C5E550FA09BCB551', '/contest/county', 200, '2024-06-13 19:53:35.714437', 0, 241085);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241087, 'countyadmin3', 'localhost:8888', 'C9EAAD2786A715B2C7BDC23CB908FA9FCE97ADE64A05B4584155735E1E972524', '/audit-board-asm-state', 200, '2024-06-13 19:53:40.719917', 0, 241086);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241088, 'countyadmin3', 'localhost:8888', '6D5D8349EB3119A1688EDDBAF6C5170203E66386955F58793007ABF1F65A1307', '/county-asm-state', 200, '2024-06-13 19:53:40.720821', 0, 241086);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241089, 'countyadmin3', 'localhost:8888', 'D5D500B0BC40C0C72C20A1B6F39FD9470835A29BAD68DF5B8C6BA39119261760', '/county-dashboard', 200, '2024-06-13 19:53:40.729703', 0, 241086);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241090, 'countyadmin3', 'localhost:8888', '1F9B4F9E13D2F907C4E3F481C4476A0C5DE5EADC012A15B3E0B4E82BCDCED20F', '/contest/county', 200, '2024-06-13 19:53:40.763667', 0, 241089);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241091, 'countyadmin3', 'localhost:8888', '4BD5A7F5FCCD78EA9980013083193BE6E1943036FDF1D0A2DEEAE8CB11FBCCB6', '/audit-board-asm-state', 200, '2024-06-13 19:53:45.764091', 0, 241090);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241092, 'countyadmin3', 'localhost:8888', '42948E279A0363BE6FFECF972E671E7B0D655BBECDD42DA4FB05CB43C403A0DA', '/county-asm-state', 200, '2024-06-13 19:53:45.765096', 0, 241090);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241093, 'countyadmin3', 'localhost:8888', 'C25B0ACEE0401222150AB2793BA55123AB1C163620DEC88B8F426CE370ABA85E', '/county-dashboard', 200, '2024-06-13 19:53:45.772032', 0, 241090);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241094, 'countyadmin3', 'localhost:8888', 'E85516CB6EEB7365E675D21034B46E153B6A29B94EB75F77933A13E3FF36417A', '/contest/county', 200, '2024-06-13 19:53:45.797831', 0, 241093);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241096, 'countyadmin3', 'localhost:8888', '62A13653D4EB72E937FA003F83CF774F80EF802EF8CCE19ECACB53607DBED38B', '/upload-file', 200, '2024-06-13 19:53:49.616436', 0, 241094);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241097, 'countyadmin3', 'localhost:8888', 'F06F2ADF11B6A78C17BBEA1E650EAA9E6AB71BE70CA76445EF69ED546A2F2EF1', '/audit-board-asm-state', 200, '2024-06-13 19:53:49.643144', 0, 241096);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241098, 'countyadmin3', 'localhost:8888', 'BC31C758F84BD0FCB228A861CEABBB0D3DC11E1E21CA22AC2D95CB297C8EEC39', '/county-asm-state', 200, '2024-06-13 19:53:49.644132', 0, 241096);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241099, 'countyadmin3', 'localhost:8888', 'C244599C09A7EC450676F215D2AB965693E17FE4C0CD14655EE8EBF5FD6A0231', '/county-dashboard', 200, '2024-06-13 19:53:49.6527', 0, 241096);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241101, 'countyadmin3', 'localhost:8888', '286D7A86F025A4495B6DAC3CDD11F33BD1E37BA5DF2AB6AF7229DEBF8FFCCF91', '/import-ballot-manifest', 200, '2024-06-13 19:53:49.662672', 0, 241096);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241102, 'countyadmin3', 'localhost:8888', 'B1DE4B59BF4E43CEB5412B5A9E98A628E4B109D4387159609E6ABE9FF3FF5FE8', '/contest/county', 200, '2024-06-13 19:53:49.688175', 0, 241101);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241103, 'countyadmin3', 'localhost:8888', 'B9182F2F215B6C9EB7900DBFE759C32B7F36E09798165E9776A2313A84030CFD', '/audit-board-asm-state', 200, '2024-06-13 19:53:49.6956', 0, 241102);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241104, 'countyadmin3', 'localhost:8888', '2A7993FCB3B0F653AA3A50CEE7A9FEB6E84DCAC8C8A302B703BA74EBAE486AC9', '/county-asm-state', 200, '2024-06-13 19:53:49.696628', 0, 241102);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241105, 'countyadmin3', 'localhost:8888', '9B1520E8EBFDF9E4A8B095FD236AEC20C9C0830A3AC6924DD2AAF06D0472C1F5', '/county-dashboard', 200, '2024-06-13 19:53:49.71341', 0, 241102);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241106, 'countyadmin3', 'localhost:8888', '67E24A832F87D0AED0DCD47B7216C0068FFFD4FE17B685A2298D5A615B91F8A1', '/contest/county', 200, '2024-06-13 19:53:49.759759', 0, 241105);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241107, 'countyadmin3', 'localhost:8888', 'EAF1F979F405427DB4A678FFE26B338DA0CB3C55A5DDC9041292303938CAAD54', '/audit-board-asm-state', 200, '2024-06-13 19:53:50.799842', 0, 241106);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241108, 'countyadmin3', 'localhost:8888', '225156FF7F7B47BDB6B79FE44E81A245FB452C14C7DB9487137128DE60A4A35F', '/county-asm-state', 200, '2024-06-13 19:53:50.800132', 0, 241106);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241109, 'countyadmin3', 'localhost:8888', 'FA23A476E6ACF74DAC2EE0FED1718A5B96FA86A24E2A365F8F5B6694056B6DD0', '/county-dashboard', 200, '2024-06-13 19:53:50.810503', 0, 241106);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241110, 'countyadmin3', 'localhost:8888', 'B7A2A455F1913CCAF793BA99E5B71EE2673861849347B091966045C2639C1194', '/contest/county', 200, '2024-06-13 19:53:50.845238', 0, 241109);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241111, 'countyadmin3', 'localhost:8888', '00FF83E98C52C1598E4F1136A3D165038EF012686599ABEF74CC1738BABB83A1', '/audit-board-asm-state', 200, '2024-06-13 19:53:55.840578', 0, 241110);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241112, 'countyadmin3', 'localhost:8888', '662B83031CD6898A318666DC6BB751917070AB98451688D57F704116CDD1637F', '/county-asm-state', 200, '2024-06-13 19:53:55.842132', 0, 241110);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241113, 'countyadmin3', 'localhost:8888', '3375380B6C8455F37B25373C69FDBCE7CD7DAA67B25141CFAE1891FB7A189414', '/county-dashboard', 200, '2024-06-13 19:53:55.850353', 0, 241110);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241114, 'countyadmin3', 'localhost:8888', 'E452862C0D4DE30E6D47F7794C223E4FC6C290032E6398734C8362CB5B89A550', '/contest/county', 200, '2024-06-13 19:53:55.871707', 0, 241113);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241115, 'countyadmin3', 'localhost:8888', '8D3438D60913DCB6BAB73B7DD30768BE8099EBD0803FC6569223F36CAA3B2DF0', '/audit-board-asm-state', 200, '2024-06-13 19:54:00.997335', 0, 241114);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241116, 'countyadmin3', 'localhost:8888', 'C6C48C4AB1FC0FF0F1D84FEFC24F1D8C8D2CED0104FC95C338BFA92B800741AF', '/county-asm-state', 200, '2024-06-13 19:54:00.999088', 0, 241114);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241117, 'countyadmin3', 'localhost:8888', 'A05452BA8DA8D2308F23A538AADAE84934A8E32D8B03FA96CE08D9B469085E6B', '/county-dashboard', 200, '2024-06-13 19:54:01.005537', 0, 241114);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241118, 'countyadmin3', 'localhost:8888', '9A87BB250E9C9058FFB55CCA9193CF35B3DCFBC2D8EC8850511CDA86D9C1F686', '/contest/county', 200, '2024-06-13 19:54:01.034022', 0, 241117);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241121, 'countyadmin3', 'localhost:8888', '609C7A9EB8F5FCEC6D14D9AB4F4236E97E0BD466264E04388F82726592950F78', '/county-dashboard', 200, '2024-06-13 19:54:07.154483', 0, 241118);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241122, 'countyadmin3', 'localhost:8888', '0D5B8AE6C1E073E2247273B8310F3439BD4893A0CCDBD3D5D9E3F78122EC62F8', '/contest/county', 200, '2024-06-13 19:54:07.205717', 0, 241121);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241125, 'countyadmin3', 'localhost:8888', '54EF711D81EF90A21BDFD942CFF557E2C06A27144C0B6D3F64918D2414E463FC', '/county-dashboard', 200, '2024-06-13 19:54:13.155748', 0, 241122);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241126, 'countyadmin3', 'localhost:8888', '7B7FC92C3CDEAD0C6F0F158820C3415D97C11F0785099FCE866725D1850AB898', '/contest/county', 200, '2024-06-13 19:54:13.183441', 0, 241125);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241129, 'countyadmin3', 'localhost:8888', 'CAEA00773AB4E87FFE27E17D268FBA5B58B528F70F13CDF0F693AC89604B3B1E', '/county-dashboard', 200, '2024-06-13 19:54:19.181561', 0, 241126);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241130, 'countyadmin3', 'localhost:8888', '4EB51F32BCCDB75254E3208248CE2B52C84ED57CEB3A972AF34A3F4C2597FA80', '/contest/county', 200, '2024-06-13 19:54:19.209046', 0, 241129);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241133, 'countyadmin3', 'localhost:8888', '2AB29EED8E58A56F26CB64DF6D5A0365D2DE11FB7CB7992A9DDE9620CE97B8DC', '/county-dashboard', 200, '2024-06-13 19:54:25.199244', 0, 241130);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241134, 'countyadmin3', 'localhost:8888', '80365B523411620390DC008B84CBC76D11C7EBD8858D7EA3E07D0771B330354A', '/contest/county', 200, '2024-06-13 19:54:25.224269', 0, 241133);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241137, 'countyadmin3', 'localhost:8888', '769CA93C89CD1F461A42895D559386609B40C5C0340A7D36616DCF3CE293F803', '/county-dashboard', 200, '2024-06-13 19:54:31.209883', 0, 241134);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241138, 'countyadmin3', 'localhost:8888', '92662ABE00722FC998F5CFA7B8DB92A5F4D4A85634915E0E76AA348FE1743FA7', '/contest/county', 200, '2024-06-13 19:54:31.234852', 0, 241137);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241119, 'countyadmin3', 'localhost:8888', 'AE317E1ADE6AA7D9A56E7AFEEDE03161DB1CE2FE3976D8FBB19B80D9D0A8F3C2', '/audit-board-asm-state', 200, '2024-06-13 19:54:07.144111', 0, 241118);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241124, 'countyadmin3', 'localhost:8888', 'A063A8FF6A18B022F89A837295477423CFAB94C4EA1D1A836F8A333EE95E0663', '/county-asm-state', 200, '2024-06-13 19:54:13.151731', 0, 241122);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241127, 'countyadmin3', 'localhost:8888', '1192F1FB14EDDBD5D9D382805F62624740E1E7B2E1FC83EDF04EBBE3B354FAA7', '/audit-board-asm-state', 200, '2024-06-13 19:54:19.174704', 0, 241126);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241132, 'countyadmin3', 'localhost:8888', '564B4FED24EFDAF3AB8477D5E217946CD0919822B69DE458E519E9A2E7F7354D', '/county-asm-state', 200, '2024-06-13 19:54:25.19179', 0, 241130);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241135, 'countyadmin3', 'localhost:8888', '4AF520BD836A41C74BCC035F97F5C2D19B50F0D53613EB4C1DDA585A39269E81', '/audit-board-asm-state', 200, '2024-06-13 19:54:31.201627', 0, 241134);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241120, 'countyadmin3', 'localhost:8888', '43D74B2CC5A087AD9AFA862B50768E2A0C6383202DE0A2C53D3AF81326C71AD9', '/county-asm-state', 200, '2024-06-13 19:54:07.145041', 0, 241118);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241123, 'countyadmin3', 'localhost:8888', '94E2DC3B5A15378051E1132AA4F0FBAE85389397438B24884FCC7EC345DF15E8', '/audit-board-asm-state', 200, '2024-06-13 19:54:13.151013', 0, 241122);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241128, 'countyadmin3', 'localhost:8888', '474AD25C6872255EF53207482B4061F87A7956C611EAE25A01C4C4B409EBC580', '/county-asm-state', 200, '2024-06-13 19:54:19.174746', 0, 241126);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241131, 'countyadmin3', 'localhost:8888', '674A0F0759697346BF54B49495EB21745D2BC421CB08FA49D8D6674CC9C9CD17', '/audit-board-asm-state', 200, '2024-06-13 19:54:25.191607', 0, 241130);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241136, 'countyadmin3', 'localhost:8888', '9F7C69B8007AE4C0AB2B9AC34CEE2DB8D49F70BB5152D9ED241938BD3107D587', '/county-asm-state', 200, '2024-06-13 19:54:31.201988', 0, 241134);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241139, 'countyadmin3', 'localhost:8888', 'E25BBF42CF859E2A7D9AF4A33167BE94F9D35012CAEE6EF644700DADF7D6E855', '/county-asm-state', 200, '2024-06-13 19:54:36.240697', 0, 241138);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241140, 'countyadmin3', 'localhost:8888', '08F710B1AF67F43F0EF67C5672BF2615CD65AE7B930885FA5C4C9AE4BF34A62E', '/audit-board-asm-state', 200, '2024-06-13 19:54:36.240131', 0, 241138);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241141, 'countyadmin3', 'localhost:8888', '9580705A39FBB35857A469F25E4E17372240E83DF19C44F2D5D2096BEBD3EFF1', '/county-dashboard', 200, '2024-06-13 19:54:36.253692', 0, 241138);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241142, 'countyadmin3', 'localhost:8888', '789C68AFD888B654781500D559B332B7DB56C0C629294D1BDBF587709E86ED82', '/contest/county', 200, '2024-06-13 19:54:36.27987', 0, 241141);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241143, 'countyadmin3', 'localhost:8888', '5EAED3A5F7F946EC2A6EADBD4BC685B61446774AF14CDB040791D719FF3451B1', '/audit-board-asm-state', 200, '2024-06-13 19:54:41.352993', 0, 241142);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241144, 'countyadmin3', 'localhost:8888', '591516040AAACF3375CA5A608DAD43869E19D035C398E8F8D5FE5773175216CF', '/county-asm-state', 200, '2024-06-13 19:54:41.354068', 0, 241142);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241145, 'countyadmin3', 'localhost:8888', '75D1A49C1DC53A354D3C2B39EC10EF61B37B300C4AF58EB542C7563A4C32CA3D', '/county-dashboard', 200, '2024-06-13 19:54:41.362539', 0, 241142);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241146, 'countyadmin3', 'localhost:8888', '37A4C0465752C0B680356BA08DFAD11A2B7843E36E6BA01760E11E2C7B0D9CB9', '/contest/county', 200, '2024-06-13 19:54:41.382842', 0, 241145);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241147, 'countyadmin3', 'localhost:8888', '872A9590D20C889654AAD73661BD1F91741390F9AA862C3A75EA4BFC35F8AC20', '/audit-board-asm-state', 200, '2024-06-13 19:54:47.377515', 0, 241146);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241148, 'countyadmin3', 'localhost:8888', 'FC7B15186FD621612B50A4AC834F17448A2DC23C04F02D4F84C10D248430AD1D', '/county-asm-state', 200, '2024-06-13 19:54:47.378627', 0, 241146);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241149, 'countyadmin3', 'localhost:8888', '3CA2E00CFACC90ADF116B405F7F079627E78F30696CB93577D2C7B86E83AD451', '/county-dashboard', 200, '2024-06-13 19:54:47.386463', 0, 241146);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241150, 'countyadmin3', 'localhost:8888', '14B8116972C37EA3E32FC7AFC11D0758EA101A43B3742302593A858C05CEFB59', '/contest/county', 200, '2024-06-13 19:54:47.40867', 0, 241149);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241151, 'countyadmin3', 'localhost:8888', 'CF04A455B158BFAC5F1050B17BED5BFAAEDFF6C0641723C2BEAFF0BB30E01305', '/audit-board-asm-state', 200, '2024-06-13 19:54:52.410565', 0, 241150);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241152, 'countyadmin3', 'localhost:8888', '57866BF7549AEF4E4FB56A0522CF1335EDBFFE6BB81F42E2D1CFBE8AE3DBBF64', '/county-asm-state', 200, '2024-06-13 19:54:52.410792', 0, 241150);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241153, 'countyadmin3', 'localhost:8888', '79F3D70E11A27D7F6C3B531274FF83D14157819F34E42FDB380E1E26E59AD788', '/county-dashboard', 200, '2024-06-13 19:54:52.420666', 0, 241150);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241154, 'countyadmin3', 'localhost:8888', 'B954DC02FAE2AA87EB56C17CC307DA7D5E32475E41544F1BD26E28C10C19EF1E', '/contest/county', 200, '2024-06-13 19:54:52.443667', 0, 241153);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241155, 'countyadmin3', 'localhost:8888', '79424A390DF170B137212932B922431454EE07CE1656A7EDDF4AC7E04248E12B', '/county-asm-state', 200, '2024-06-13 19:54:57.447237', 0, 241154);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241156, 'countyadmin3', 'localhost:8888', '68DCCE0E46C464D31F1570338F7C9FEE1EC4A721DF19FBC137A1F8D9A075602B', '/audit-board-asm-state', 200, '2024-06-13 19:54:57.447371', 0, 241154);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241157, 'countyadmin3', 'localhost:8888', 'B77ADF1DA1B6758DB30E3173EC930DD80BF91AE3AD342969BB93FBBC6C3236F0', '/county-dashboard', 200, '2024-06-13 19:54:57.457615', 0, 241154);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241158, 'countyadmin3', 'localhost:8888', 'CFC80A6259F9AB30D6F3C3D856C2D5BF46F99555CF53B898FA2A7AD9BFD85C89', '/contest/county', 200, '2024-06-13 19:54:57.483874', 0, 241157);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241159, 'countyadmin3', 'localhost:8888', '64BED1C7035A0F7F419F64409692187753ABCA9A871E6155817C7A54C2CC2B31', '/audit-board-asm-state', 200, '2024-06-13 19:55:02.486978', 0, 241158);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241160, 'countyadmin3', 'localhost:8888', '6149A1A91674105842D47FA9A8F4F32EAFA29C357CFAAC5AE1ED280BD4B56A8C', '/county-asm-state', 200, '2024-06-13 19:55:02.487619', 0, 241158);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241161, 'countyadmin3', 'localhost:8888', '5ACA7FFEE01C4B62D1E5001748610B3FF90861A325DB88C0E63CE05855C7D889', '/county-dashboard', 200, '2024-06-13 19:55:02.493419', 0, 241158);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241162, 'countyadmin3', 'localhost:8888', '54DA32BD85BBF185CA86ECB6E387CCD4E4E3A30F0DE64EB0973F9BDB0EE60702', '/contest/county', 200, '2024-06-13 19:55:02.523292', 0, 241161);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241163, 'countyadmin3', 'localhost:8888', '959AEC2B123E6DF550CBE0C3C4BEC3143736992A55AABE323819F70BD71E6B67', '/audit-board-asm-state', 200, '2024-06-13 19:55:09.508304', 0, 241162);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241164, 'countyadmin3', 'localhost:8888', 'C35333133955678151ADF9341D4DD675A970D849AF33B7012FA0E222D9134DF0', '/county-asm-state', 200, '2024-06-13 19:55:09.509853', 0, 241162);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241165, 'countyadmin3', 'localhost:8888', 'ED74822E668AEF22AC607FAEE96D86B7EF67537F525FED76BDA91EA013553497', '/county-dashboard', 200, '2024-06-13 19:55:09.517393', 0, 241162);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241166, 'countyadmin3', 'localhost:8888', '4B0A18CDD5C78A889B367CE1483715F7FE649F1C7F5C8D352203EF0D5A0C2E9E', '/contest/county', 200, '2024-06-13 19:55:09.574386', 0, 241165);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241167, 'countyadmin3', 'localhost:8888', '3C4AD5397864A730031435928669C48556F624D50ABDC5E2598EC6AE7AC08491', '/audit-board-asm-state', 200, '2024-06-13 19:55:15.548263', 0, 241166);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241168, 'countyadmin3', 'localhost:8888', 'D4BD14397277B175A49F78D9E1E8479518B3C31BC0E0717930221BEE5A603CBC', '/county-asm-state', 200, '2024-06-13 19:55:15.549334', 0, 241166);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241169, 'countyadmin3', 'localhost:8888', '55CC4DB7DFA8382C43811C702888A4D37060428C47FF17A41ED6366BC6A07626', '/county-dashboard', 200, '2024-06-13 19:55:15.556405', 0, 241166);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241170, 'countyadmin3', 'localhost:8888', '2B34F1CB71B592D6230469C61A9781C4FC3BE9A76925639C13588DC67FF493FA', '/contest/county', 200, '2024-06-13 19:55:15.583366', 0, 241169);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241171, 'countyadmin3', 'localhost:8888', '1B272787B14205555849F95524885233DDA5501864970A6F9E9799C2DA5E2C32', '/county-asm-state', 200, '2024-06-13 19:55:21.553166', 0, 241170);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241172, 'countyadmin3', 'localhost:8888', '20AA7606A7570542A9FE1A37C2064C5F6205C2446CE2BB9471ED271C757A852E', '/audit-board-asm-state', 200, '2024-06-13 19:55:21.553122', 0, 241170);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241173, 'countyadmin3', 'localhost:8888', '5839CF2F55E06AE8D9482CE2460E7D28BC0F2A13FD8C766A2AD531136BD66818', '/county-dashboard', 200, '2024-06-13 19:55:21.559071', 0, 241170);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241174, 'countyadmin3', 'localhost:8888', '7D1C7CEBAFD27F2464FC1FB51197F6CECA6E9ED53352A85A120E2DAB6FD77858', '/contest/county', 200, '2024-06-13 19:55:21.581203', 0, 241173);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241176, 'countyadmin3', 'localhost:8888', '0677D5573D13CC3F78A4641F3558680711CF99F9F0C71F1F94729E99B4B35B2C', '/county-asm-state', 200, '2024-06-13 19:55:26.585802', 0, 241174);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241175, 'countyadmin3', 'localhost:8888', 'DC84762048983479E1E37C3469D30541405BD0DC99175000403D3766DB5DF28E', '/audit-board-asm-state', 200, '2024-06-13 19:55:26.585197', 0, 241174);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241177, 'countyadmin3', 'localhost:8888', '40EC49C84863853B0F773FBE874719A7B0C287A079F8E9469E319DC2601ABC0B', '/county-dashboard', 200, '2024-06-13 19:55:26.599222', 0, 241174);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241178, 'countyadmin3', 'localhost:8888', '46720856AD759162D16888888E3FA7C0586833A190E176D86801CE0072694CE6', '/contest/county', 200, '2024-06-13 19:55:26.63896', 0, 241177);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241179, 'countyadmin3', 'localhost:8888', 'C504B890352BE829DA6E1B373B4D47A85F51E3260B8D442DB1C3B35B4E26962F', '/audit-board-asm-state', 200, '2024-06-13 19:55:31.637808', 0, 241178);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241184, 'countyadmin3', 'localhost:8888', '86404BC12668E6F6C0196B53CF6C16F29A8EB722E3194BBBA404951871CBF480', '/county-asm-state', 200, '2024-06-13 19:55:36.675288', 0, 241182);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241188, 'countyadmin3', 'localhost:8888', '1F3B34BA49C099C1958B6E74972844433A0A3A8DBFF7FB84510A1AAB74C19C43', '/county-asm-state', 200, '2024-06-13 19:55:41.716189', 0, 241186);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241191, 'countyadmin3', 'localhost:8888', '64735A76D632EF1860ADBE60D8483A2D0EC0AC51DDC067E321391AAE3F888B88', '/audit-board-asm-state', 200, '2024-06-13 19:55:46.761859', 0, 241190);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241180, 'countyadmin3', 'localhost:8888', '81E50F33A555E9D8D80C12E339941F0269942C8768D3B28B74CDA66E1E0E53DD', '/county-asm-state', 200, '2024-06-13 19:55:31.640396', 0, 241178);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241183, 'countyadmin3', 'localhost:8888', '4A3E209A9997C133089C32C2AFE17A302E5E295A3A89112F7D62FFC526EE9CE7', '/audit-board-asm-state', 200, '2024-06-13 19:55:36.674881', 0, 241182);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241187, 'countyadmin3', 'localhost:8888', '546AB2445DA91B1FA5DCDA792D6C9EC066BCAEF9AE1E13B6636B80E42B8AA111', '/audit-board-asm-state', 200, '2024-06-13 19:55:41.714833', 0, 241186);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241192, 'countyadmin3', 'localhost:8888', '5CD85F9BB8F26DC599505BD70C2734C906813749843B4EEB02EA68B849616CBA', '/county-asm-state', 200, '2024-06-13 19:55:46.762608', 0, 241190);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241181, 'countyadmin3', 'localhost:8888', 'D4DE7240626ABAB5A103CB17DE8069491CBC598A6690F2A977359D0B3A5AA957', '/county-dashboard', 200, '2024-06-13 19:55:31.647485', 0, 241178);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241182, 'countyadmin3', 'localhost:8888', '495F87941787E0D8FB636335E5104201DA608B1E20F30B1F29AA6FD1F917F4F0', '/contest/county', 200, '2024-06-13 19:55:31.672795', 0, 241181);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241185, 'countyadmin3', 'localhost:8888', 'C5D9FBA91FFFACC44571B668CA66197D37D442CDBCF5404B3508EA6CE995D198', '/county-dashboard', 200, '2024-06-13 19:55:36.683803', 0, 241182);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241186, 'countyadmin3', 'localhost:8888', '6DBB7D41B9FBB33C69949D0AF3B97D0EFBBB9AD2A53C4C1FBCF193FCB2D78EAE', '/contest/county', 200, '2024-06-13 19:55:36.711359', 0, 241185);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241189, 'countyadmin3', 'localhost:8888', 'BA41403E275ED19FDF5BD0146FC89DB50D138C75AFE2455987CE99DD78C1B831', '/county-dashboard', 200, '2024-06-13 19:55:41.727287', 0, 241186);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241190, 'countyadmin3', 'localhost:8888', '0741B30C3FB270676A662D9F4871E7E208F641A59CEBDF1F6E5D5D7E9C1409B3', '/contest/county', 200, '2024-06-13 19:55:41.761188', 0, 241189);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241193, 'countyadmin3', 'localhost:8888', '8B566F6A066E25699981F53EFFA8B6D645CEE367FEC32AFB89930DCC8C2C18A1', '/county-dashboard', 200, '2024-06-13 19:55:46.768759', 0, 241190);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241194, 'countyadmin3', 'localhost:8888', 'F5825D0A2BEA6A6A88AC7AFA241E5D714A71821B2EE2511C201307BF9151B338', '/contest/county', 200, '2024-06-13 19:55:46.79542', 0, 241193);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241195, 'countyadmin3', 'localhost:8888', '53EBEA1F23E580D6F4443E0B691288EBCDA19252AB0237F2DA6E951E159BC8F3', '/county-asm-state', 200, '2024-06-13 19:55:51.801042', 0, 241194);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241196, 'countyadmin3', 'localhost:8888', '10A02D7B5C0F64FFF5698AD0C21CB7720D5071E7D7659885B02F7FCF490E6772', '/audit-board-asm-state', 200, '2024-06-13 19:55:51.800777', 0, 241194);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241197, 'countyadmin3', 'localhost:8888', 'CB3C088EF162C52D9CA736B34DBE34F21BBE19878CF1EA2542EDE22BEA27B727', '/county-dashboard', 200, '2024-06-13 19:55:51.816305', 0, 241194);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241198, 'countyadmin3', 'localhost:8888', '80486647AC23374E5E848447B9947D9448A115C20A0C7595C2BE3B3CFCBEE02E', '/contest/county', 200, '2024-06-13 19:55:51.844064', 0, 241197);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241199, 'countyadmin3', 'localhost:8888', '8B013F5A48068C2EA340F0B92F4B2204AC756D375EC4EC394C9487CA8C11E560', '/audit-board-asm-state', 200, '2024-06-13 19:55:57.827053', 0, 241198);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241200, 'countyadmin3', 'localhost:8888', '698662286E92534CE4BAB44DE686E6993D0316B9E88FE1AFF94DAC09431F9BB9', '/county-asm-state', 200, '2024-06-13 19:55:57.827478', 0, 241198);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241201, 'countyadmin3', 'localhost:8888', '75611CF2ECE42080C746E9BC69E012CC087D51D9547CCB7954EA1F553147E67A', '/county-dashboard', 200, '2024-06-13 19:55:57.836559', 0, 241198);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241202, 'countyadmin3', 'localhost:8888', '08D983898465F8B77FEDBCF19271064A4E6B541D672F9D96E94B6353C8EE6B08', '/contest/county', 200, '2024-06-13 19:55:57.865892', 0, 241201);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241203, 'countyadmin3', 'localhost:8888', 'ECFC89755B9E165A3578C371CBB4969CDF77856FDC758F97ABD521C4134B391D', '/audit-board-asm-state', 200, '2024-06-13 19:56:03.846732', 0, 241202);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241204, 'countyadmin3', 'localhost:8888', 'C116FE8AC9B5BF2143C20F3B5A55F404F5AE1E7D3A2C10E7DC39126CC6B6A599', '/county-asm-state', 200, '2024-06-13 19:56:03.847365', 0, 241202);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241205, 'countyadmin3', 'localhost:8888', 'C0266173C4656CD873A87B2037287DA73955F709795E31E2A5ABB168B06F7A2A', '/county-dashboard', 200, '2024-06-13 19:56:03.855282', 0, 241202);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241206, 'countyadmin3', 'localhost:8888', 'BB8CE5B921FAEB19C29A46CCD8095FC183F85590AF76BE9449B08E8433D8C8E3', '/contest/county', 200, '2024-06-13 19:56:03.881387', 0, 241205);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241207, 'countyadmin3', 'localhost:8888', 'EC35365551ED193DB7B936D16F70A9236D687BA0429206EDCF1AD0E31679B3EB', '/county-asm-state', 200, '2024-06-13 19:56:08.884839', 0, 241206);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241208, 'countyadmin3', 'localhost:8888', '63287678BB99BB6292CEC8596053CEDBAB08ED3A6D926A85BF07874B4B756991', '/audit-board-asm-state', 200, '2024-06-13 19:56:08.885043', 0, 241206);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241209, 'countyadmin3', 'localhost:8888', '4AF5A5E07E142C8625C3823B7F9ABAFD0C137E734B95C61BDFDB220042FF3E3B', '/county-dashboard', 200, '2024-06-13 19:56:08.89424', 0, 241206);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241210, 'countyadmin3', 'localhost:8888', '58DF5D09B8677AB76ADD5F80B043709B7CE603C1FCBF2AD85EA52CB65FC95EFA', '/contest/county', 200, '2024-06-13 19:56:08.920802', 0, 241209);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241211, 'countyadmin3', 'localhost:8888', 'DCFA7ECA1716C5486A97327960292DE4FBFDA2371AD6FD7C46C8CE7B353B5D97', '/county-asm-state', 200, '2024-06-13 19:56:13.923195', 0, 241210);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241212, 'countyadmin3', 'localhost:8888', 'EA81DEFFABF6BED8B310D6DC5FB6D8A4342472D0353ED028CFAAB8E281D31997', '/audit-board-asm-state', 200, '2024-06-13 19:56:13.923324', 0, 241210);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241213, 'countyadmin3', 'localhost:8888', 'DC0D93D17E0DABA01B01514CF4F739DE04C22D265BFEE0F0910A97E03E1859FE', '/county-dashboard', 200, '2024-06-13 19:56:13.932802', 0, 241210);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241214, 'countyadmin3', 'localhost:8888', 'DCA3E4EA82D4E7B4D5085CB1841B69AFBF3F28D57CED64CDA366716CFC196D5E', '/contest/county', 200, '2024-06-13 19:56:13.959516', 0, 241213);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241215, 'countyadmin3', 'localhost:8888', '7F2AB9EF41C9CA8EEF50BA2E8EAB819314F121114B9B73D8F57E394F8E90811C', '/audit-board-asm-state', 200, '2024-06-13 19:56:18.969908', 0, 241214);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241216, 'countyadmin3', 'localhost:8888', '913BF4F5B6BEB9FF45CCCDEE7255072B09B96D85C6B6DA7AFADC40BB41B5CBAA', '/county-asm-state', 200, '2024-06-13 19:56:18.972029', 0, 241214);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241217, 'countyadmin3', 'localhost:8888', '8066FCD7B2DED644183EC5E8B25AABC0BA7B35D8E66D22F4D55F2CF0BB2260AD', '/county-dashboard', 200, '2024-06-13 19:56:18.979394', 0, 241214);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241218, 'countyadmin3', 'localhost:8888', 'C936C70760585FC411BA3ED6599990A206EA163EDD879643BE1F612CE4BB78F8', '/contest/county', 200, '2024-06-13 19:56:19.021935', 0, 241217);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241220, 'countyadmin3', 'localhost:8888', '3592295D7410C844B26F1A4B6C2A83F7ECF7DBE6793E5550AE5E93169DEF0AE1', '/county-asm-state', 200, '2024-06-13 19:56:24.986206', 0, 241218);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241219, 'countyadmin3', 'localhost:8888', '151079B6842CD3DF7DC5F4B1D8E2DDD4CF3430B3A9CB8075701F9DC76C0A3860', '/audit-board-asm-state', 200, '2024-06-13 19:56:24.985207', 0, 241218);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241221, 'countyadmin3', 'localhost:8888', '69A53FEAD87F772B9AF3B622D694B79DCFE8C853A84D68257478343015820D1E', '/county-dashboard', 200, '2024-06-13 19:56:24.994474', 0, 241218);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241222, 'countyadmin3', 'localhost:8888', 'EB1FA5074F10361ECF58A44F4884C85F19F952136D0B7B9D08D4860C861862EB', '/contest/county', 200, '2024-06-13 19:56:25.018958', 0, 241221);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241223, 'countyadmin3', 'localhost:8888', '04B1F3C5778AAA91517CC332A0D65385BA4A03A286B4640F38B7C5355D85DC56', '/audit-board-asm-state', 200, '2024-06-13 19:56:30.025707', 0, 241222);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241224, 'countyadmin3', 'localhost:8888', 'E3925218DBE3BD30C611358A6D59018367B285029DC9993CE9B0B32AD017D359', '/county-asm-state', 200, '2024-06-13 19:56:30.02804', 0, 241222);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241225, 'countyadmin3', 'localhost:8888', 'A3F4EF14B2472BE82F447D1946B93F539340F6DDBD60575C4D4FDB76237DFC00', '/county-dashboard', 200, '2024-06-13 19:56:30.035023', 0, 241222);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241226, 'countyadmin3', 'localhost:8888', '8ABF89A7EB7149FD4FBD72B158D93338412F21F11F2DFA86F4DE141E067203D8', '/contest/county', 200, '2024-06-13 19:56:30.057698', 0, 241225);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241227, 'countyadmin3', 'localhost:8888', '9BEB12FEE979C6E6D4809E4F40FEAB3C3F898D90D1F8406C7068229AB28A7DC5', '/county-asm-state', 200, '2024-06-13 19:56:35.065514', 0, 241226);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241228, 'countyadmin3', 'localhost:8888', '4840E5193C16F7F77B97576A621B91F2D84D5EF2225BB628E09591B297610D48', '/audit-board-asm-state', 200, '2024-06-13 19:56:35.065514', 0, 241226);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241229, 'countyadmin3', 'localhost:8888', 'ED293CA105ECB360ACC28E160097F1A05FFE726F53BB17C769223E7382DAD7AA', '/county-dashboard', 200, '2024-06-13 19:56:35.07474', 0, 241226);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241230, 'countyadmin3', 'localhost:8888', 'BA380665715E94667AC2F510EB65A57A52C2449BB364091DDEF97D53836E01D4', '/contest/county', 200, '2024-06-13 19:56:35.099927', 0, 241229);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241231, 'countyadmin3', 'localhost:8888', '3DAF7C91630AD5EA5ACC5754CE6FCE08FCB8A9A16AE7A2EF2F301ADF1FF494DF', '/county-asm-state', 200, '2024-06-13 19:56:41.085413', 0, 241230);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241232, 'countyadmin3', 'localhost:8888', 'B2031590E02A9CC07479F41A987DD9B241AEBCC9DF8FB9C621989699B84E64DB', '/audit-board-asm-state', 200, '2024-06-13 19:56:41.085858', 0, 241230);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241233, 'countyadmin3', 'localhost:8888', 'F9AD5F6E4A2FED7320918525F17FA591AB5FC9C092B4AD474807F1AFF72632B0', '/county-dashboard', 200, '2024-06-13 19:56:41.093456', 0, 241230);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241234, 'countyadmin3', 'localhost:8888', '62845C1EB0BCE2BAB132FBE11C58434343024FFA949D242C5353E47624DAAA4F', '/contest/county', 200, '2024-06-13 19:56:41.115801', 0, 241233);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241235, 'countyadmin3', 'localhost:8888', 'C49EA3319F768E5AC3C405E7C617F5AA966AD697C65147896BD56D9B20B45513', '/audit-board-asm-state', 200, '2024-06-13 19:56:47.095466', 0, 241234);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241236, 'countyadmin3', 'localhost:8888', '407305136F393B6DBADC27F06C7D1BC2C0E8D94D4866587D8403F1DD9EB945FF', '/county-asm-state', 200, '2024-06-13 19:56:47.096152', 0, 241234);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241237, 'countyadmin3', 'localhost:8888', 'C706387A3631EF8F72A1EBAF5D5EF9820902CB96A50A1A9324F7E10AD883B7F8', '/county-dashboard', 200, '2024-06-13 19:56:47.10317', 0, 241234);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241238, 'countyadmin3', 'localhost:8888', '522C85B25CEFB5C3EFEE0D66F04270611645C564919105D7EC0B05CA818D225D', '/contest/county', 200, '2024-06-13 19:56:47.130655', 0, 241237);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241239, 'countyadmin3', 'localhost:8888', 'D9C0CD0CBC7D84BE2C93465343F3309D15635B7C4E464447A95C57B47EDD5652', '/audit-board-asm-state', 200, '2024-06-13 19:56:52.139194', 0, 241238);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241240, 'countyadmin3', 'localhost:8888', '2CB2A42E8681DACAAE71CF2558ADB099ED6218870FB295FC8FF309D264548D54', '/county-asm-state', 200, '2024-06-13 19:56:52.140182', 0, 241238);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241241, 'countyadmin3', 'localhost:8888', '133869F88E13E45639F8DDD3F3D6D15CE39287EC35F8970DFD6F9E4BA2617693', '/county-dashboard', 200, '2024-06-13 19:56:52.149935', 0, 241238);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241242, 'countyadmin3', 'localhost:8888', '9C94A5133F5E93BFD047B2AC50173B0C5DC968BA4C32AE7C51E254C38FC63C27', '/contest/county', 200, '2024-06-13 19:56:52.181549', 0, 241241);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241243, 'countyadmin3', 'localhost:8888', '87E3CBA7784DCC749A912648AB1A9AB65288D4CEBAA5CBB0BA3D06BB95662B0A', '/audit-board-asm-state', 200, '2024-06-13 19:56:57.183625', 0, 241242);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241244, 'countyadmin3', 'localhost:8888', '31BBD58709583A17D0A396C5F872FC2A1D6351D111FF64F0D313D0A0205FF004', '/county-asm-state', 200, '2024-06-13 19:56:57.183678', 0, 241242);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241245, 'countyadmin3', 'localhost:8888', '263D05E93F6B7740BAD1BDDC666CEA46E0D63E67F77454A3AEAACC0B13E50FDB', '/county-dashboard', 200, '2024-06-13 19:56:57.190583', 0, 241242);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241246, 'countyadmin3', 'localhost:8888', '8B60B736E2351433642E5FBA76FDD600A8CD7D5EB6A911F1E12391C12567A7B2', '/contest/county', 200, '2024-06-13 19:56:57.217137', 0, 241245);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241247, 'countyadmin3', 'localhost:8888', '08DBC250B73284DD0FEA3CD1651F12248E2C93B8288FB2FC588FE2C35802E55C', '/audit-board-asm-state', 200, '2024-06-13 19:57:02.223368', 0, 241246);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241248, 'countyadmin3', 'localhost:8888', '72B782836C10E90A697653D5C66A252E9FDCAFF9052FFAA2DE0992112B0C1852', '/county-asm-state', 200, '2024-06-13 19:57:02.224245', 0, 241246);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241249, 'countyadmin3', 'localhost:8888', '80D836311D425825B14A8CA3F3D0B7437E160390003533D30D12BCC771B5F15B', '/county-dashboard', 200, '2024-06-13 19:57:02.228875', 0, 241246);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241250, 'countyadmin3', 'localhost:8888', '74B25899FCC2E267CEAE70E4A1977F6E048F710E739A1B0E85A9888BEBD2ECE1', '/contest/county', 200, '2024-06-13 19:57:02.252187', 0, 241249);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241251, 'countyadmin3', 'localhost:8888', 'E1DD4CB5A6DA84681E92525E8D6BDA9BBFAC1F8C436333C735517FDE6DCEA522', '/audit-board-asm-state', 200, '2024-06-13 19:57:07.262382', 0, 241250);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241252, 'countyadmin3', 'localhost:8888', 'FE1315C507E57DD39BFDE25270A32CD5AF59504CD5D9EE1EEFE9191430343E6D', '/county-asm-state', 200, '2024-06-13 19:57:07.263164', 0, 241250);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241253, 'countyadmin3', 'localhost:8888', '03C6B9E4A9D78AE683C159EBB714EC9299C8DB40C48E7C2A63CA052D3121FC73', '/county-dashboard', 200, '2024-06-13 19:57:07.271169', 0, 241250);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241254, 'countyadmin3', 'localhost:8888', '3744B42F5AB08B4E72BA24F09F2B360B48DC1CD7FEF4002456A48E90AB965E3E', '/contest/county', 200, '2024-06-13 19:57:07.29661', 0, 241253);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241255, 'countyadmin3', 'localhost:8888', 'B026807D0AD8561CE785BB25FF633F95058152609868760A5E81BBEBDA8438FD', '/audit-board-asm-state', 200, '2024-06-13 19:57:13.27331', 0, 241254);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241256, 'countyadmin3', 'localhost:8888', 'DC67870DE9CDD9448B49761DF2CC519937CD547E81740968BB5BB7AA36F36A8F', '/county-asm-state', 200, '2024-06-13 19:57:13.274432', 0, 241254);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241257, 'countyadmin3', 'localhost:8888', '6156CBF856FF82A2318F1DCE6854A6937A24B7782FBA5FE005960C9C91FAB2F0', '/county-dashboard', 200, '2024-06-13 19:57:13.281524', 0, 241254);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241258, 'countyadmin3', 'localhost:8888', '77973F12B78D7AF60C10D8966EC780F2DFC1B76A35BC28C441D2EFD5120400EA', '/contest/county', 200, '2024-06-13 19:57:13.307514', 0, 241257);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241260, 'countyadmin3', 'localhost:8888', '2A6C1F5D8A147C77450420A42B46A4D2D183FED819C5C6BBFCF80917FAB85D57', '/audit-board-asm-state', 200, '2024-06-13 19:57:18.312678', 0, 241258);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241259, 'countyadmin3', 'localhost:8888', 'EC8331322F70815657B637558326D90FF608AFB55F2BB58F1546DAEFE498F918', '/county-asm-state', 200, '2024-06-13 19:57:18.312635', 0, 241258);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241261, 'countyadmin3', 'localhost:8888', '6F1437D54681E5718713245288BE86006B115C4632E44B765EAD3C386E60E7E0', '/county-dashboard', 200, '2024-06-13 19:57:18.317316', 0, 241258);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241262, 'countyadmin3', 'localhost:8888', 'DB1CE98F270A438D4E990A830DC6AC9F8CBC6DCC2F8ADC71AEE0B463C2F5DEA7', '/contest/county', 200, '2024-06-13 19:57:18.346431', 0, 241261);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241263, 'countyadmin3', 'localhost:8888', '02662FF1DE583210560B315A52EEE0D9619FFC39D0757AD059BB14B496E043F5', '/audit-board-asm-state', 200, '2024-06-13 19:57:23.349482', 0, 241262);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241264, 'countyadmin3', 'localhost:8888', '061EC7BE0B0C9089C6030BE9A3FF73ACDB7CA24472267ACA5CBB3C9836C11AEB', '/county-asm-state', 200, '2024-06-13 19:57:23.350236', 0, 241262);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241265, 'countyadmin3', 'localhost:8888', 'BF58CC6FB42CFB0F646E43B4AC7A6D6A0320C37D35CE2B2DAD0C8BFDB5B62936', '/county-dashboard', 200, '2024-06-13 19:57:23.357236', 0, 241262);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241266, 'countyadmin3', 'localhost:8888', '0AFBA6B1C09A77ADD95142E9E1BB7AE2A599C8857FA5EDC8AA7ABDAE7F1D0BA3', '/contest/county', 200, '2024-06-13 19:57:23.380486', 0, 241265);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241267, 'countyadmin3', 'localhost:8888', 'C5B2873ABBABA373574B5206ECB1B8898B6532F3B1A872C188D9086C54977A7B', '/audit-board-asm-state', 200, '2024-06-13 19:57:28.382627', 0, 241266);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241268, 'countyadmin3', 'localhost:8888', '6024144A981099FE263D610EC5CC9E59C86F3F2E065DE52868D8D93210790F6E', '/county-asm-state', 200, '2024-06-13 19:57:28.383918', 0, 241266);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241269, 'countyadmin3', 'localhost:8888', 'B58A039A3182A8506A864F41356C7C068BA18297B311C433DBCC61A9DF102B33', '/county-dashboard', 200, '2024-06-13 19:57:28.390056', 0, 241266);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241270, 'countyadmin3', 'localhost:8888', '802EFDEE8C857504861576DE1B927B9C5432C59ECF0EC98B72D59EE8C5770F07', '/contest/county', 200, '2024-06-13 19:57:28.413212', 0, 241269);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241271, 'countyadmin3', 'localhost:8888', '82D50D5DB55302D28DA2934C6DCC4E8CFCD1059DDFD0EF2F0266B2AB29FE80A3', '/audit-board-asm-state', 200, '2024-06-13 19:57:33.417281', 0, 241270);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241272, 'countyadmin3', 'localhost:8888', '2773BEAFB885F9B1916402822ACA9E197F6C4A1001887BFA32A66CF5C19561C4', '/county-asm-state', 200, '2024-06-13 19:57:33.418541', 0, 241270);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241273, 'countyadmin3', 'localhost:8888', '0A5BB5C961C460AFB04C4993831BDEE8E076933428BB0241237D2607ABF73B0F', '/county-dashboard', 200, '2024-06-13 19:57:33.425131', 0, 241270);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241274, 'countyadmin3', 'localhost:8888', 'F572931E1F0C655CE0EFE9E2C635FC9957056914C586B3C36D24A2A8EE1E8BA4', '/contest/county', 200, '2024-06-13 19:57:33.449183', 0, 241273);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241275, 'countyadmin3', 'localhost:8888', 'BDDAE0AC22F4DACC78580BEFCA636CC61D843A6AE18B768A412CE0FE3640D3EB', '/audit-board-asm-state', 200, '2024-06-13 19:57:39.437567', 0, 241274);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241276, 'countyadmin3', 'localhost:8888', '8C1805FFC1D7700DB3CDC831CAC48EA32DA9DEE3C5AE0612F719EA1D89727EC6', '/county-asm-state', 200, '2024-06-13 19:57:39.438546', 0, 241274);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241277, 'countyadmin3', 'localhost:8888', '80B1BFD9E5C6114B01C90FF176965D7929CC0DF1B27043DDEA80C3D80DC365D7', '/county-dashboard', 200, '2024-06-13 19:57:39.445789', 0, 241274);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241278, 'countyadmin3', 'localhost:8888', 'F5BA7B35833EFD078463EB4457A35B8AFCE13A49BF1EDD72C2C887FA1A932CD4', '/contest/county', 200, '2024-06-13 19:57:39.472011', 0, 241277);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241281, 'countyadmin3', 'localhost:8888', 'A13A0D72FEC7A5190C4D5478E3C1068A140E9AD1FE905D36611BDC1C10EF306D', '/county-dashboard', 200, '2024-06-13 19:57:45.450521', 0, 241278);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241282, 'countyadmin3', 'localhost:8888', '1B9AA27FAC09C033E03B729194AA31A28ACB200BDC1858117A362C4B53FD34BA', '/contest/county', 200, '2024-06-13 19:57:45.468641', 0, 241281);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241283, 'countyadmin3', 'localhost:8888', 'B395C0EC2B6430CD48D2F923CEB869FF7FDFB7EBEC7AE1CA85ACF26A65EE387B', '/county-dashboard', 200, '2024-06-13 19:57:51.466447', 0, 241282);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241284, 'countyadmin3', 'localhost:8888', '89E263AA12F5491B20D718A91AD8838A2BA02FAFD38B7FC00D2FD92129DCC2DF', '/audit-board-asm-state', 200, '2024-06-13 19:57:51.472051', 0, 241283);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241287, 'countyadmin3', 'localhost:8888', '7374D9286D44412E4C3BD06207866A5B1AEF97FB1C0427D0514588A1AF7FF382', '/audit-board-asm-state', 200, '2024-06-13 19:57:57.471857', 0, 241286);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241292, 'countyadmin3', 'localhost:8888', '6ACC5669FA9B4835817BB149DEE3AF13158305C9CA8CBD363C0A311AFBEFD7E6', '/county-asm-state', 200, '2024-06-13 19:58:03.491558', 0, 241290);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241279, 'countyadmin3', 'localhost:8888', 'B5A61B4A5D70DBDFEBE88E153911EE5F77FC991F9B5B5DEE66B3E360C33376F6', '/audit-board-asm-state', 200, '2024-06-13 19:57:45.444387', 0, 241278);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241288, 'countyadmin3', 'localhost:8888', '6E4BAFB3703DC8507E44A93E4F273E0B71338865B72DE21C3E66745FC51A4862', '/county-asm-state', 200, '2024-06-13 19:57:57.472488', 0, 241286);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241291, 'countyadmin3', 'localhost:8888', '71007FD117C5F1113A31BD4F9EFD26B0A14EF06C8C20D625D2662956AA253743', '/audit-board-asm-state', 200, '2024-06-13 19:58:03.490478', 0, 241290);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241280, 'countyadmin3', 'localhost:8888', '04BA791BD259745BABAB1374E0EC3C0D6FFB9A6BB6471BAC6DEF31F3D64C914A', '/county-asm-state', 200, '2024-06-13 19:57:45.444415', 0, 241278);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241285, 'countyadmin3', 'localhost:8888', '3A94F9A634EF3E4118A817191149A39EB35075A9C2B6984971542B2892B6ADD0', '/county-asm-state', 200, '2024-06-13 19:57:51.473359', 0, 241283);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241286, 'countyadmin3', 'localhost:8888', '47C3F3B6E57FFF1BC6EDA93FB6F10F2F2F7C52E9039160E90E6FD9336EE8D64B', '/contest/county', 200, '2024-06-13 19:57:51.49414', 0, 241285);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241289, 'countyadmin3', 'localhost:8888', 'AD5F8945C0C3A8D014E5E2A65734C523D5CF315A54DF94C7F34B352AC1316D64', '/county-dashboard', 200, '2024-06-13 19:57:57.482609', 0, 241286);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241290, 'countyadmin3', 'localhost:8888', 'E85914981CCE8E694817DA47EF111E4CC66E650D417625B786DB1EDD1BF7DD4B', '/contest/county', 200, '2024-06-13 19:57:57.502537', 0, 241289);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241293, 'countyadmin3', 'localhost:8888', '1BB96747859A6BC7DA23746071748AF77E95163AE100B5679533F5019A7B6D33', '/county-dashboard', 200, '2024-06-13 19:58:03.501561', 0, 241290);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (241294, 'countyadmin3', 'localhost:8888', '5D841FEC900F8A211F03C94FD7237BBD7289E06E4DF9D2DDA4531ED80707DB5B', '/contest/county', 200, '2024-06-13 19:58:03.525506', 0, 241293);


--
-- Data for Name: round; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: tribute; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: uploaded_file; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (240488, '7760C6690CAACD2568CA80ED10B8D3362D561D4573890DA867B2CAC10EE89659', 14, 17313, 'ThreeCandidatesTenVotesPlusPlurality.csv', 1718, '2024-06-13 19:42:48.902499', 1, '{"success":true,"importedCount":10}', 'IMPORTED', '7760C6690CAACD2568CA80ED10B8D3362D561D4573890DA867B2CAC10EE89659', 1);
INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (240543, '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 2, 17314, 'ThreeCandidatesTenVotes_Manifest.csv', 78, '2024-06-13 19:43:21.05893', 1, '{"success":true,"importedCount":1,"errorMessage":null,"errorRowNum":null,"errorRowContent":null}', 'IMPORTED', '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 1);
INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (240590, '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 2, 17316, 'ThreeCandidatesTenVotes_Manifest.csv', 78, '2024-06-13 19:44:06.224713', 1, '{"success":true,"importedCount":1,"errorMessage":null,"errorRowNum":null,"errorRowContent":null}', 'IMPORTED', '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 2);
INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (240618, 'D2EE3F29D1CCB8B0F4B790493EDC2F41B4081DCC1D760277ACA27268140D166C', 14, 17317, 'ThreeCandidatesTenVotes.csv', 1364, '2024-06-13 19:44:27.083812', 1, '{"success":true,"importedCount":10}', 'IMPORTED', 'D2EE3F29D1CCB8B0F4B790493EDC2F41B4081DCC1D760277ACA27268140D166C', 2);
INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (241040, 'ECF373ABB783C2A6577FD46BF2437C728498E88304A6FC2CCEFB61A1082B3556', 14, 17322, 'ThreeCandidatesTenVotesPlusInconsistentPlurality.csv', 2389, '2024-06-13 19:53:11.723821', 1, '{"success":true,"importedCount":10}', 'IMPORTED', 'ECF373ABB783C2A6577FD46BF2437C728498E88304A6FC2CCEFB61A1082B3556', 3);
INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (241095, '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 2, 17324, 'ThreeCandidatesTenVotes_Manifest.csv', 78, '2024-06-13 19:53:49.606724', 1, '{"success":true,"importedCount":1,"errorMessage":null,"errorRowNum":null,"errorRowContent":null}', 'IMPORTED', '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 3);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: corlaadmin
--

SELECT pg_catalog.setval('public.hibernate_sequence', 241294, true);


--
-- Name: administrator administrator_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.administrator
    ADD CONSTRAINT administrator_pkey PRIMARY KEY (id);


--
-- Name: asm_state asm_state_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.asm_state
    ADD CONSTRAINT asm_state_pkey PRIMARY KEY (id);


--
-- Name: audit_board audit_board_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.audit_board
    ADD CONSTRAINT audit_board_pkey PRIMARY KEY (dashboard_id, index);


--
-- Name: audit_intermediate_report audit_intermediate_report_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.audit_intermediate_report
    ADD CONSTRAINT audit_intermediate_report_pkey PRIMARY KEY (dashboard_id, index);


--
-- Name: audit_investigation_report audit_investigation_report_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.audit_investigation_report
    ADD CONSTRAINT audit_investigation_report_pkey PRIMARY KEY (dashboard_id, index);


--
-- Name: ballot_manifest_info ballot_manifest_info_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.ballot_manifest_info
    ADD CONSTRAINT ballot_manifest_info_pkey PRIMARY KEY (id);


--
-- Name: cast_vote_record cast_vote_record_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.cast_vote_record
    ADD CONSTRAINT cast_vote_record_pkey PRIMARY KEY (id);


--
-- Name: comparison_audit comparison_audit_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.comparison_audit
    ADD CONSTRAINT comparison_audit_pkey PRIMARY KEY (id);


--
-- Name: contest_choice contest_choice_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_choice
    ADD CONSTRAINT contest_choice_pkey PRIMARY KEY (contest_id, index);


--
-- Name: contest_comparison_audit_disagreement contest_comparison_audit_disagreement_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_comparison_audit_disagreement
    ADD CONSTRAINT contest_comparison_audit_disagreement_pkey PRIMARY KEY (contest_comparison_audit_id, cvr_audit_info_id);


--
-- Name: contest_comparison_audit_discrepancy contest_comparison_audit_discrepancy_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_comparison_audit_discrepancy
    ADD CONSTRAINT contest_comparison_audit_discrepancy_pkey PRIMARY KEY (contest_comparison_audit_id, cvr_audit_info_id);


--
-- Name: contest contest_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest
    ADD CONSTRAINT contest_pkey PRIMARY KEY (id);


--
-- Name: contest_result contest_result_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_result
    ADD CONSTRAINT contest_result_pkey PRIMARY KEY (id);


--
-- Name: contest_vote_total contest_vote_total_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_vote_total
    ADD CONSTRAINT contest_vote_total_pkey PRIMARY KEY (result_id, choice);


--
-- Name: contests_to_contest_results contests_to_contest_results_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contests_to_contest_results
    ADD CONSTRAINT contests_to_contest_results_pkey PRIMARY KEY (contest_result_id, contest_id);


--
-- Name: counties_to_contest_results counties_to_contest_results_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.counties_to_contest_results
    ADD CONSTRAINT counties_to_contest_results_pkey PRIMARY KEY (contest_result_id, county_id);


--
-- Name: county_contest_comparison_audit_disagreement county_contest_comparison_audit_disagreement_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit_disagreement
    ADD CONSTRAINT county_contest_comparison_audit_disagreement_pkey PRIMARY KEY (county_contest_comparison_audit_id, cvr_audit_info_id);


--
-- Name: county_contest_comparison_audit_discrepancy county_contest_comparison_audit_discrepancy_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit_discrepancy
    ADD CONSTRAINT county_contest_comparison_audit_discrepancy_pkey PRIMARY KEY (county_contest_comparison_audit_id, cvr_audit_info_id);


--
-- Name: county_contest_comparison_audit county_contest_comparison_audit_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit
    ADD CONSTRAINT county_contest_comparison_audit_pkey PRIMARY KEY (id);


--
-- Name: county_contest_result county_contest_result_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_result
    ADD CONSTRAINT county_contest_result_pkey PRIMARY KEY (id);


--
-- Name: county_contest_vote_total county_contest_vote_total_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_vote_total
    ADD CONSTRAINT county_contest_vote_total_pkey PRIMARY KEY (result_id, choice);


--
-- Name: county_dashboard county_dashboard_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_dashboard
    ADD CONSTRAINT county_dashboard_pkey PRIMARY KEY (id);


--
-- Name: county_dashboard_to_comparison_audit county_dashboard_to_comparison_audit_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_dashboard_to_comparison_audit
    ADD CONSTRAINT county_dashboard_to_comparison_audit_pkey PRIMARY KEY (dashboard_id, comparison_audit_id);


--
-- Name: county county_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county
    ADD CONSTRAINT county_pkey PRIMARY KEY (id);


--
-- Name: cvr_audit_info cvr_audit_info_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.cvr_audit_info
    ADD CONSTRAINT cvr_audit_info_pkey PRIMARY KEY (id);


--
-- Name: cvr_contest_info cvr_contest_info_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.cvr_contest_info
    ADD CONSTRAINT cvr_contest_info_pkey PRIMARY KEY (cvr_id, index);


--
-- Name: dos_dashboard dos_dashboard_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.dos_dashboard
    ADD CONSTRAINT dos_dashboard_pkey PRIMARY KEY (id);


--
-- Name: county_contest_result idx_ccr_county_contest; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_result
    ADD CONSTRAINT idx_ccr_county_contest UNIQUE (county_id, contest_id);


--
-- Name: contest_result idx_cr_contest; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_result
    ADD CONSTRAINT idx_cr_contest UNIQUE (contest_name);


--
-- Name: log log_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: round round_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.round
    ADD CONSTRAINT round_pkey PRIMARY KEY (dashboard_id, index);


--
-- Name: tribute tribute_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.tribute
    ADD CONSTRAINT tribute_pkey PRIMARY KEY (id);


--
-- Name: county_dashboard uk_6lcjowb4rw9xav8nqnf5v2klk; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_dashboard
    ADD CONSTRAINT uk_6lcjowb4rw9xav8nqnf5v2klk UNIQUE (county_id);


--
-- Name: administrator uk_esogmqxeek1uwdyhxvubme3qf; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.administrator
    ADD CONSTRAINT uk_esogmqxeek1uwdyhxvubme3qf UNIQUE (username);


--
-- Name: county uk_npkepig28dujo4w98bkmaclhp; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county
    ADD CONSTRAINT uk_npkepig28dujo4w98bkmaclhp UNIQUE (name);


--
-- Name: contests_to_contest_results uk_t1qahmm5y32ovxtqxne8i7ou0; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contests_to_contest_results
    ADD CONSTRAINT uk_t1qahmm5y32ovxtqxne8i7ou0 UNIQUE (contest_id);


--
-- Name: contest_choice uka8o6q5yeepuy2cgnrbx3l1rka; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_choice
    ADD CONSTRAINT uka8o6q5yeepuy2cgnrbx3l1rka UNIQUE (contest_id, name);


--
-- Name: contest ukdv45ptogm326acwp45hm46uaf; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest
    ADD CONSTRAINT ukdv45ptogm326acwp45hm46uaf UNIQUE (name, county_id, description, votes_allowed);


--
-- Name: cast_vote_record uniquecvr; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.cast_vote_record
    ADD CONSTRAINT uniquecvr UNIQUE (county_id, imprinted_id, record_type, revision);


--
-- Name: uploaded_file uploaded_file_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.uploaded_file
    ADD CONSTRAINT uploaded_file_pkey PRIMARY KEY (id);


--
-- Name: idx_bmi_county; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_bmi_county ON public.ballot_manifest_info USING btree (county_id);


--
-- Name: idx_bmi_seqs; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_bmi_seqs ON public.ballot_manifest_info USING btree (sequence_start, sequence_end);


--
-- Name: idx_ccca_dashboard; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_ccca_dashboard ON public.county_contest_comparison_audit USING btree (dashboard_id);


--
-- Name: idx_ccr_contest; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_ccr_contest ON public.county_contest_result USING btree (contest_id);


--
-- Name: idx_ccr_county; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_ccr_county ON public.county_contest_result USING btree (county_id);


--
-- Name: idx_contest_name; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_contest_name ON public.contest USING btree (name);


--
-- Name: idx_contest_name_county_description_votes_allowed; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_contest_name_county_description_votes_allowed ON public.contest USING btree (name, county_id, description, votes_allowed);


--
-- Name: idx_cvr_county_cvr_number; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_cvr_county_cvr_number ON public.cast_vote_record USING btree (county_id, cvr_number);


--
-- Name: idx_cvr_county_cvr_number_type; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_cvr_county_cvr_number_type ON public.cast_vote_record USING btree (county_id, cvr_number, record_type);


--
-- Name: idx_cvr_county_imprinted_id_type; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_cvr_county_imprinted_id_type ON public.cast_vote_record USING btree (county_id, imprinted_id, record_type);


--
-- Name: idx_cvr_county_sequence_number_type; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_cvr_county_sequence_number_type ON public.cast_vote_record USING btree (county_id, sequence_number, record_type);


--
-- Name: idx_cvr_county_type; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_cvr_county_type ON public.cast_vote_record USING btree (county_id, record_type);


--
-- Name: idx_cvr_uri; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_cvr_uri ON public.cast_vote_record USING btree (uri);


--
-- Name: idx_cvrci_uri; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_cvrci_uri ON public.cvr_contest_info USING btree (county_id, contest_id);


--
-- Name: idx_uploaded_file_county; Type: INDEX; Schema: public; Owner: corlaadmin
--

CREATE INDEX idx_uploaded_file_county ON public.uploaded_file USING btree (county_id);


--
-- Name: county_dashboard fk1bg939xcuwen7fohfkdx10ueb; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_dashboard
    ADD CONSTRAINT fk1bg939xcuwen7fohfkdx10ueb FOREIGN KEY (county_id) REFERENCES public.county(id);


--
-- Name: counties_to_contest_results fk1ke574b6yqdc8ylu5xyqrounp; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.counties_to_contest_results
    ADD CONSTRAINT fk1ke574b6yqdc8ylu5xyqrounp FOREIGN KEY (county_id) REFERENCES public.county(id);


--
-- Name: counties_to_contest_results fk2h2muw290os109yqar5p4onms; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.counties_to_contest_results
    ADD CONSTRAINT fk2h2muw290os109yqar5p4onms FOREIGN KEY (contest_result_id) REFERENCES public.contest_result(id);


--
-- Name: cvr_audit_info fk2n0rxgwa4njtnsm8l4hwc8khy; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.cvr_audit_info
    ADD CONSTRAINT fk2n0rxgwa4njtnsm8l4hwc8khy FOREIGN KEY (acvr_id) REFERENCES public.cast_vote_record(id);


--
-- Name: county_contest_comparison_audit_discrepancy fk39q8rjoa19c4fdjmv4m9iir06; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit_discrepancy
    ADD CONSTRAINT fk39q8rjoa19c4fdjmv4m9iir06 FOREIGN KEY (county_contest_comparison_audit_id) REFERENCES public.county_contest_comparison_audit(id);


--
-- Name: contest_comparison_audit_discrepancy fk3la5frd86i29mlwjd8akjgpwp; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_comparison_audit_discrepancy
    ADD CONSTRAINT fk3la5frd86i29mlwjd8akjgpwp FOREIGN KEY (cvr_audit_info_id) REFERENCES public.cvr_audit_info(id);


--
-- Name: county_dashboard fk6rb04heyw700ep1ynn0r31xv3; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_dashboard
    ADD CONSTRAINT fk6rb04heyw700ep1ynn0r31xv3 FOREIGN KEY (cvr_file_id) REFERENCES public.uploaded_file(id);


--
-- Name: county_contest_comparison_audit_disagreement fk7yt9a4fjcdctwmftwwsksdnma; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit_disagreement
    ADD CONSTRAINT fk7yt9a4fjcdctwmftwwsksdnma FOREIGN KEY (county_contest_comparison_audit_id) REFERENCES public.county_contest_comparison_audit(id);


--
-- Name: uploaded_file fk8gh92iwaes042cc1uvi6714yj; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.uploaded_file
    ADD CONSTRAINT fk8gh92iwaes042cc1uvi6714yj FOREIGN KEY (county_id) REFERENCES public.county(id);


--
-- Name: county_contest_comparison_audit fk8te9gv7q10wxbhg5pgttbj3mv; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit
    ADD CONSTRAINT fk8te9gv7q10wxbhg5pgttbj3mv FOREIGN KEY (contest_id) REFERENCES public.contest(id);


--
-- Name: contest fk932jeyl0hqd21fmakkco5tfa3; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest
    ADD CONSTRAINT fk932jeyl0hqd21fmakkco5tfa3 FOREIGN KEY (county_id) REFERENCES public.county(id);


--
-- Name: county_contest_comparison_audit_disagreement fk9lhehe4o2dgqde06pxycydlu6; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit_disagreement
    ADD CONSTRAINT fk9lhehe4o2dgqde06pxycydlu6 FOREIGN KEY (cvr_audit_info_id) REFERENCES public.cvr_audit_info(id);


--
-- Name: county_contest_comparison_audit fkag9u8fyqni2ehb2dtqop4pox8; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit
    ADD CONSTRAINT fkag9u8fyqni2ehb2dtqop4pox8 FOREIGN KEY (contest_result_id) REFERENCES public.contest_result(id);


--
-- Name: audit_board fkai07es6t6bdw8hidapxxa5xnp; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.audit_board
    ADD CONSTRAINT fkai07es6t6bdw8hidapxxa5xnp FOREIGN KEY (dashboard_id) REFERENCES public.county_dashboard(id);


--
-- Name: contest_comparison_audit_discrepancy fkcajmftu1xv4jehnm5qhc35j9n; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_comparison_audit_discrepancy
    ADD CONSTRAINT fkcajmftu1xv4jehnm5qhc35j9n FOREIGN KEY (contest_comparison_audit_id) REFERENCES public.comparison_audit(id);


--
-- Name: county_contest_result fkcuw4fb39imk9pyw360bixorm3; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_result
    ADD CONSTRAINT fkcuw4fb39imk9pyw360bixorm3 FOREIGN KEY (county_id) REFERENCES public.county(id);


--
-- Name: cvr_audit_info fkdks3q3g0srpa44rkkoj3ilve6; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.cvr_audit_info
    ADD CONSTRAINT fkdks3q3g0srpa44rkkoj3ilve6 FOREIGN KEY (cvr_id) REFERENCES public.cast_vote_record(id);


--
-- Name: audit_investigation_report fkdox65w3y11hyhtcba5hrekq9u; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.audit_investigation_report
    ADD CONSTRAINT fkdox65w3y11hyhtcba5hrekq9u FOREIGN KEY (dashboard_id) REFERENCES public.county_dashboard(id);


--
-- Name: county_dashboard_to_comparison_audit fkds9j4o8el1f4nepf2677hvs5o; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_dashboard_to_comparison_audit
    ADD CONSTRAINT fkds9j4o8el1f4nepf2677hvs5o FOREIGN KEY (dashboard_id) REFERENCES public.county_dashboard(id);


--
-- Name: cvr_contest_info fke2fqsfmj0uqq311l4c3i0nt7r; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.cvr_contest_info
    ADD CONSTRAINT fke2fqsfmj0uqq311l4c3i0nt7r FOREIGN KEY (contest_id) REFERENCES public.contest(id);


--
-- Name: round fke3kvxe5r43a4xmeugp8lnme9e; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.round
    ADD CONSTRAINT fke3kvxe5r43a4xmeugp8lnme9e FOREIGN KEY (dashboard_id) REFERENCES public.county_dashboard(id);


--
-- Name: contest_vote_total fkfjk25vmtng6dv2ejlp8eopy34; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_vote_total
    ADD CONSTRAINT fkfjk25vmtng6dv2ejlp8eopy34 FOREIGN KEY (result_id) REFERENCES public.contest_result(id);


--
-- Name: log fkfw6ikly73lha9g9em13n3kat4; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.log
    ADD CONSTRAINT fkfw6ikly73lha9g9em13n3kat4 FOREIGN KEY (previous_entry) REFERENCES public.log(id);


--
-- Name: administrator fkh6rcfib1ishmhry9ctgm16gie; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.administrator
    ADD CONSTRAINT fkh6rcfib1ishmhry9ctgm16gie FOREIGN KEY (county_id) REFERENCES public.county(id);


--
-- Name: contests_to_contest_results fki7qed7v0pkbi2bnd5fvujtp7; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contests_to_contest_results
    ADD CONSTRAINT fki7qed7v0pkbi2bnd5fvujtp7 FOREIGN KEY (contest_id) REFERENCES public.contest(id);


--
-- Name: contest_to_audit fkid09bdp5ifs6m4cnyw3ycyo1s; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_to_audit
    ADD CONSTRAINT fkid09bdp5ifs6m4cnyw3ycyo1s FOREIGN KEY (contest_id) REFERENCES public.contest(id);


--
-- Name: county_contest_vote_total fkip5dfccmp5x5ubssgar17qpwk; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_vote_total
    ADD CONSTRAINT fkip5dfccmp5x5ubssgar17qpwk FOREIGN KEY (result_id) REFERENCES public.county_contest_result(id);


--
-- Name: contest_to_audit fkjlw9bpyarqou0j26hq7mmq8qm; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_to_audit
    ADD CONSTRAINT fkjlw9bpyarqou0j26hq7mmq8qm FOREIGN KEY (dashboard_id) REFERENCES public.dos_dashboard(id);


--
-- Name: audit_intermediate_report fkmvj30ou8ik3u7avvycsw0vjx8; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.audit_intermediate_report
    ADD CONSTRAINT fkmvj30ou8ik3u7avvycsw0vjx8 FOREIGN KEY (dashboard_id) REFERENCES public.county_dashboard(id);


--
-- Name: comparison_audit fkn14qkca2ilirtpr4xctw960pe; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.comparison_audit
    ADD CONSTRAINT fkn14qkca2ilirtpr4xctw960pe FOREIGN KEY (contest_result_id) REFERENCES public.contest_result(id);


--
-- Name: contest_choice fknsr30axyiavqhyupxohtfy0sl; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_choice
    ADD CONSTRAINT fknsr30axyiavqhyupxohtfy0sl FOREIGN KEY (contest_id) REFERENCES public.contest(id);


--
-- Name: county_contest_result fkon2wldpt0279jqex3pjx1mhm7; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_result
    ADD CONSTRAINT fkon2wldpt0279jqex3pjx1mhm7 FOREIGN KEY (contest_id) REFERENCES public.contest(id);


--
-- Name: county_contest_comparison_audit_discrepancy fkpe25737bc4mpt170y53ba7il2; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit_discrepancy
    ADD CONSTRAINT fkpe25737bc4mpt170y53ba7il2 FOREIGN KEY (cvr_audit_info_id) REFERENCES public.cvr_audit_info(id);


--
-- Name: contest_comparison_audit_disagreement fkpfdns930t0qv905vbwhgcxnl2; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_comparison_audit_disagreement
    ADD CONSTRAINT fkpfdns930t0qv905vbwhgcxnl2 FOREIGN KEY (cvr_audit_info_id) REFERENCES public.cvr_audit_info(id);


--
-- Name: contests_to_contest_results fkr1jgmnxu2fbbvujdh3srjmot9; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contests_to_contest_results
    ADD CONSTRAINT fkr1jgmnxu2fbbvujdh3srjmot9 FOREIGN KEY (contest_result_id) REFERENCES public.contest_result(id);


--
-- Name: county_dashboard fkrs4q3gwfv0up7swx7q1q6xlwo; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_dashboard
    ADD CONSTRAINT fkrs4q3gwfv0up7swx7q1q6xlwo FOREIGN KEY (manifest_file_id) REFERENCES public.uploaded_file(id);


--
-- Name: cvr_contest_info fkrsovkqe4e839e0aels78u7a3g; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.cvr_contest_info
    ADD CONSTRAINT fkrsovkqe4e839e0aels78u7a3g FOREIGN KEY (cvr_id) REFERENCES public.cast_vote_record(id);


--
-- Name: county_dashboard_to_comparison_audit fksliko6ckjcr7wvmicuqyreopl; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_dashboard_to_comparison_audit
    ADD CONSTRAINT fksliko6ckjcr7wvmicuqyreopl FOREIGN KEY (comparison_audit_id) REFERENCES public.comparison_audit(id);


--
-- Name: county_contest_comparison_audit fksycb9uto400qabgb97d4ihbat; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.county_contest_comparison_audit
    ADD CONSTRAINT fksycb9uto400qabgb97d4ihbat FOREIGN KEY (dashboard_id) REFERENCES public.county_dashboard(id);


--
-- Name: contest_comparison_audit_disagreement fkt490by57jb58ubropwn7kmadi; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_comparison_audit_disagreement
    ADD CONSTRAINT fkt490by57jb58ubropwn7kmadi FOREIGN KEY (contest_comparison_audit_id) REFERENCES public.comparison_audit(id);


--
-- PostgreSQL database dump complete
--

