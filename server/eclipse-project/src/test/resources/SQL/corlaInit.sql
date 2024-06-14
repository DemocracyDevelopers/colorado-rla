--
-- PostgreSQL database dump
--

-- Dumped from database version 14.12 (Ubuntu 14.12-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.12 (Ubuntu 14.12-0ubuntu0.22.04.1)

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
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- CREATE SCHEMA public;


-- ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
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
-- Name: assertion_seq; Type: SEQUENCE; Schema: public; Owner: corlaadmin
--

CREATE SEQUENCE public.assertion_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.assertion_seq OWNER TO corlaadmin;

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


ALTER TABLE public.hibernate_sequence OWNER TO corlaadmin;

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
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-101, 'County Administrator 1', NULL, NULL, 'COUNTY', 'countyadmin1', 0, 1);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-102, 'County Administrator 2', NULL, NULL, 'COUNTY', 'countyadmin2', 0, 2);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-103, 'County Administrator 3', NULL, NULL, 'COUNTY', 'countyadmin3', 0, 3);
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


--
-- Data for Name: asm_state; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44639, 'us.freeandfair.corla.asm.DoSDashboardASM', 'DoS', 'us.freeandfair.corla.asm.ASMState$DoSDashboardState', 'DOS_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44640, 'us.freeandfair.corla.asm.CountyDashboardASM', '44', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44641, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '44', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44642, 'us.freeandfair.corla.asm.CountyDashboardASM', '45', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44643, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '45', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44644, 'us.freeandfair.corla.asm.CountyDashboardASM', '46', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44645, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '46', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44646, 'us.freeandfair.corla.asm.CountyDashboardASM', '47', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44647, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '47', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44648, 'us.freeandfair.corla.asm.CountyDashboardASM', '48', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44649, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '48', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44650, 'us.freeandfair.corla.asm.CountyDashboardASM', '49', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44651, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '49', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44652, 'us.freeandfair.corla.asm.CountyDashboardASM', '50', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44653, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '50', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44654, 'us.freeandfair.corla.asm.CountyDashboardASM', '51', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44655, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '51', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44656, 'us.freeandfair.corla.asm.CountyDashboardASM', '52', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44657, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '52', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44658, 'us.freeandfair.corla.asm.CountyDashboardASM', '53', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44659, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '53', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44660, 'us.freeandfair.corla.asm.CountyDashboardASM', '10', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44661, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '10', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44662, 'us.freeandfair.corla.asm.CountyDashboardASM', '54', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44663, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '54', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44664, 'us.freeandfair.corla.asm.CountyDashboardASM', '11', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44665, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '11', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44666, 'us.freeandfair.corla.asm.CountyDashboardASM', '55', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44667, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '55', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44668, 'us.freeandfair.corla.asm.CountyDashboardASM', '12', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44669, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '12', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44670, 'us.freeandfair.corla.asm.CountyDashboardASM', '56', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44671, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '56', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44672, 'us.freeandfair.corla.asm.CountyDashboardASM', '13', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44673, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '13', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44674, 'us.freeandfair.corla.asm.CountyDashboardASM', '57', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44675, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '57', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44676, 'us.freeandfair.corla.asm.CountyDashboardASM', '14', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44677, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '14', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44678, 'us.freeandfair.corla.asm.CountyDashboardASM', '58', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44679, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '58', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44680, 'us.freeandfair.corla.asm.CountyDashboardASM', '15', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44681, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '15', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44682, 'us.freeandfair.corla.asm.CountyDashboardASM', '59', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44683, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '59', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44684, 'us.freeandfair.corla.asm.CountyDashboardASM', '16', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44685, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '16', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44686, 'us.freeandfair.corla.asm.CountyDashboardASM', '17', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44687, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '17', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44688, 'us.freeandfair.corla.asm.CountyDashboardASM', '18', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44689, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '18', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44690, 'us.freeandfair.corla.asm.CountyDashboardASM', '19', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44691, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '19', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44692, 'us.freeandfair.corla.asm.CountyDashboardASM', '1', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44693, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '1', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44694, 'us.freeandfair.corla.asm.CountyDashboardASM', '2', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44695, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '2', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44696, 'us.freeandfair.corla.asm.CountyDashboardASM', '3', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44697, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '3', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44698, 'us.freeandfair.corla.asm.CountyDashboardASM', '4', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44699, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '4', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44700, 'us.freeandfair.corla.asm.CountyDashboardASM', '5', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44701, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '5', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44702, 'us.freeandfair.corla.asm.CountyDashboardASM', '6', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44703, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '6', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44704, 'us.freeandfair.corla.asm.CountyDashboardASM', '7', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44705, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '7', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44706, 'us.freeandfair.corla.asm.CountyDashboardASM', '8', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44707, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '8', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44708, 'us.freeandfair.corla.asm.CountyDashboardASM', '9', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44709, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '9', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44710, 'us.freeandfair.corla.asm.CountyDashboardASM', '60', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44711, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '60', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44712, 'us.freeandfair.corla.asm.CountyDashboardASM', '61', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44713, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '61', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44714, 'us.freeandfair.corla.asm.CountyDashboardASM', '62', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44715, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '62', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44716, 'us.freeandfair.corla.asm.CountyDashboardASM', '63', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44717, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '63', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44718, 'us.freeandfair.corla.asm.CountyDashboardASM', '20', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44719, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '20', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44720, 'us.freeandfair.corla.asm.CountyDashboardASM', '64', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44721, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '64', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44722, 'us.freeandfair.corla.asm.CountyDashboardASM', '21', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44723, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '21', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44724, 'us.freeandfair.corla.asm.CountyDashboardASM', '22', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44725, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '22', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44726, 'us.freeandfair.corla.asm.CountyDashboardASM', '23', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44727, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '23', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44728, 'us.freeandfair.corla.asm.CountyDashboardASM', '24', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44729, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '24', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44730, 'us.freeandfair.corla.asm.CountyDashboardASM', '25', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44731, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '25', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44732, 'us.freeandfair.corla.asm.CountyDashboardASM', '26', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44733, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '26', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44734, 'us.freeandfair.corla.asm.CountyDashboardASM', '27', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44735, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '27', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44736, 'us.freeandfair.corla.asm.CountyDashboardASM', '28', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44737, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '28', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44738, 'us.freeandfair.corla.asm.CountyDashboardASM', '29', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44739, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '29', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44740, 'us.freeandfair.corla.asm.CountyDashboardASM', '30', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44741, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '30', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44742, 'us.freeandfair.corla.asm.CountyDashboardASM', '31', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44743, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '31', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44744, 'us.freeandfair.corla.asm.CountyDashboardASM', '32', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44745, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '32', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44746, 'us.freeandfair.corla.asm.CountyDashboardASM', '33', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44747, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '33', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44748, 'us.freeandfair.corla.asm.CountyDashboardASM', '34', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44749, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '34', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44750, 'us.freeandfair.corla.asm.CountyDashboardASM', '35', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44751, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '35', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44752, 'us.freeandfair.corla.asm.CountyDashboardASM', '36', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44753, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '36', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44754, 'us.freeandfair.corla.asm.CountyDashboardASM', '37', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44755, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '37', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44756, 'us.freeandfair.corla.asm.CountyDashboardASM', '38', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44757, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '38', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44758, 'us.freeandfair.corla.asm.CountyDashboardASM', '39', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44759, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '39', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44760, 'us.freeandfair.corla.asm.CountyDashboardASM', '40', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44761, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '40', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44762, 'us.freeandfair.corla.asm.CountyDashboardASM', '41', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44763, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '41', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44764, 'us.freeandfair.corla.asm.CountyDashboardASM', '42', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44765, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '42', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44766, 'us.freeandfair.corla.asm.CountyDashboardASM', '43', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44767, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '43', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);


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



--
-- Data for Name: cast_vote_record; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: comparison_audit; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contest; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contest_choice; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



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



--
-- Data for Name: county_contest_vote_total; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: county_dashboard; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (44, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.872368', 0, '{}', '{}', 0, 44, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (45, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.882501', 0, '{}', '{}', 0, 45, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (46, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.888767', 0, '{}', '{}', 0, 46, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (47, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.894559', 0, '{}', '{}', 0, 47, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (48, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.900234', 0, '{}', '{}', 0, 48, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (49, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.905729', 0, '{}', '{}', 0, 49, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (50, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.911399', 0, '{}', '{}', 0, 50, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (51, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.916735', 0, '{}', '{}', 0, 51, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (52, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.922152', 0, '{}', '{}', 0, 52, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (53, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.927412', 0, '{}', '{}', 0, 53, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (10, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.93256', 0, '{}', '{}', 0, 10, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (54, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.937945', 0, '{}', '{}', 0, 54, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (11, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.943517', 0, '{}', '{}', 0, 11, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (55, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.948438', 0, '{}', '{}', 0, 55, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (12, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.953269', 0, '{}', '{}', 0, 12, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (56, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.957856', 0, '{}', '{}', 0, 56, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (13, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.962357', 0, '{}', '{}', 0, 13, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (57, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.96651', 0, '{}', '{}', 0, 57, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (14, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.970294', 0, '{}', '{}', 0, 14, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (58, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.974075', 0, '{}', '{}', 0, 58, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (15, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.978389', 0, '{}', '{}', 0, 15, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (59, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.982609', 0, '{}', '{}', 0, 59, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (16, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.9864', 0, '{}', '{}', 0, 16, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (17, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.990747', 0, '{}', '{}', 0, 17, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (18, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.994852', 0, '{}', '{}', 0, 18, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (19, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:01.998974', 0, '{}', '{}', 0, 19, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (1, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.003174', 0, '{}', '{}', 0, 1, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (2, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.007701', 0, '{}', '{}', 0, 2, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (3, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.011965', 0, '{}', '{}', 0, 3, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (4, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.016309', 0, '{}', '{}', 0, 4, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (5, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.020429', 0, '{}', '{}', 0, 5, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (6, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.025741', 0, '{}', '{}', 0, 6, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (7, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.029951', 0, '{}', '{}', 0, 7, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (8, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.034429', 0, '{}', '{}', 0, 8, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (9, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.038766', 0, '{}', '{}', 0, 9, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (60, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.042957', 0, '{}', '{}', 0, 60, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (61, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.047306', 0, '{}', '{}', 0, 61, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (62, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.051943', 0, '{}', '{}', 0, 62, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (63, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.056411', 0, '{}', '{}', 0, 63, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (20, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.060835', 0, '{}', '{}', 0, 20, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (64, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.064731', 0, '{}', '{}', 0, 64, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (21, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.068753', 0, '{}', '{}', 0, 21, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (22, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.073424', 0, '{}', '{}', 0, 22, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (23, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.078047', 0, '{}', '{}', 0, 23, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (24, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.082284', 0, '{}', '{}', 0, 24, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (25, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.08637', 0, '{}', '{}', 0, 25, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (26, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.090554', 0, '{}', '{}', 0, 26, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (27, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.094556', 0, '{}', '{}', 0, 27, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (28, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.098595', 0, '{}', '{}', 0, 28, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (29, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.102622', 0, '{}', '{}', 0, 29, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (30, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.106647', 0, '{}', '{}', 0, 30, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (31, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.110718', 0, '{}', '{}', 0, 31, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (32, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.114617', 0, '{}', '{}', 0, 32, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (33, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.118543', 0, '{}', '{}', 0, 33, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (34, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.122478', 0, '{}', '{}', 0, 34, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (35, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.126505', 0, '{}', '{}', 0, 35, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (36, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.130361', 0, '{}', '{}', 0, 36, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (37, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.134456', 0, '{}', '{}', 0, 37, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (38, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.138468', 0, '{}', '{}', 0, 38, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (39, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.14244', 0, '{}', '{}', 0, 39, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (40, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.146486', 0, '{}', '{}', 0, 40, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (41, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.150592', 0, '{}', '{}', 0, 41, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (42, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.154801', 0, '{}', '{}', 0, 42, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (43, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-05 16:10:02.159136', 0, '{}', '{}', 0, 43, NULL, NULL);


--
-- Data for Name: county_dashboard_to_comparison_audit; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: cvr_audit_info; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: cvr_contest_info; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: dos_dashboard; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.dos_dashboard (id, canonical_choices, canonical_contests, election_date, election_type, public_meeting_date, risk_limit, seed, version) VALUES (0, '{}', '{}', NULL, NULL, NULL, NULL, NULL, 0);


--
-- Data for Name: log; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: round; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: tribute; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: uploaded_file; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Name: assertion_seq; Type: SEQUENCE SET; Schema: public; Owner: corlaadmin
--

SELECT pg_catalog.setval('public.assertion_seq', 1, false);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: corlaadmin
--

SELECT pg_catalog.setval('public.hibernate_sequence', 44767, true);


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

