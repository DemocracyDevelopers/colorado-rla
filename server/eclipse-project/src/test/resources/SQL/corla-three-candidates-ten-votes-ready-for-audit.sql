--
-- PostgreSQL database dump
--

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
-- Name: assertion; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.assertion (
    assertion_type character varying(31) NOT NULL,
    id bigint NOT NULL,
    contest_name character varying(255) NOT NULL,
    current_risk numeric(19,2) NOT NULL,
    difficulty double precision NOT NULL,
    diluted_margin numeric(19,2) NOT NULL,
    estimated_samples_to_audit integer NOT NULL,
    loser character varying(255) NOT NULL,
    margin integer NOT NULL,
    one_vote_over_count integer NOT NULL,
    one_vote_under_count integer NOT NULL,
    optimistic_samples_to_audit integer NOT NULL,
    other_count integer NOT NULL,
    two_vote_over_count integer NOT NULL,
    two_vote_under_count integer NOT NULL,
    version bigint,
    winner character varying(255) NOT NULL
);


ALTER TABLE public.assertion OWNER TO corlaadmin;

--
-- Name: assertion_context; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.assertion_context (
    id bigint NOT NULL,
    assumed_continuing character varying(255) NOT NULL
);


ALTER TABLE public.assertion_context OWNER TO corlaadmin;

--
-- Name: assertion_discrepancies; Type: TABLE; Schema: public; Owner: corlaadmin
--

CREATE TABLE public.assertion_discrepancies (
    id bigint NOT NULL,
    discrepancy integer NOT NULL,
    cvr_id bigint NOT NULL
);


ALTER TABLE public.assertion_discrepancies OWNER TO corlaadmin;

--
-- Name: assertion_id_seq; Type: SEQUENCE; Schema: public; Owner: corlaadmin
--

CREATE SEQUENCE public.assertion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.assertion_id_seq OWNER TO corlaadmin;

--
-- Name: assertion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: corlaadmin
--

ALTER SEQUENCE public.assertion_id_seq OWNED BY public.assertion.id;


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
    audit_type character varying(31) NOT NULL,
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
-- Name: assertion id; Type: DEFAULT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.assertion ALTER COLUMN id SET DEFAULT nextval('public.assertion_id_seq'::regclass);


--
-- Data for Name: administrator; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-2, 'State Administrator 2', NULL, NULL, 'STATE', 'stateadmin2', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-3, 'State Administrator 3', NULL, NULL, 'STATE', 'stateadmin3', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-4, 'State Administrator 4', NULL, NULL, 'STATE', 'stateadmin4', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-5, 'State Administrator 5', NULL, NULL, 'STATE', 'stateadmin5', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-6, 'State Administrator 6', NULL, NULL, 'STATE', 'stateadmin6', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-7, 'State Administrator 7', NULL, NULL, 'STATE', 'stateadmin7', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-8, 'State Administrator 8', NULL, NULL, 'STATE', 'stateadmin8', 0, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-101, 'County Administrator 1', NULL, NULL, 'COUNTY', 'countyadmin1', 0, 1);
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
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-1, 'State Administrator 1', '2024-06-17 16:56:49.078321', NULL, 'STATE', 'stateadmin1', 2, NULL);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-102, 'County Administrator 2', '2024-06-17 16:56:16.536931', NULL, 'COUNTY', 'countyadmin2', 2, 2);
INSERT INTO public.administrator (id, full_name, last_login_time, last_logout_time, type, username, version, county_id) VALUES (-103, 'County Administrator 3', '2024-06-17 16:55:26.261389', '2024-06-17 16:56:07.547182', 'COUNTY', 'countyadmin3', 3, 3);


--
-- Data for Name: asm_state; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (2, 'us.freeandfair.corla.asm.CountyDashboardASM', '44', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (3, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '44', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (4, 'us.freeandfair.corla.asm.CountyDashboardASM', '45', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (5, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '45', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (6, 'us.freeandfair.corla.asm.CountyDashboardASM', '46', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (7, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '46', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (8, 'us.freeandfair.corla.asm.CountyDashboardASM', '47', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (9, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '47', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (10, 'us.freeandfair.corla.asm.CountyDashboardASM', '48', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (11, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '48', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (12, 'us.freeandfair.corla.asm.CountyDashboardASM', '49', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (13, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '49', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (14, 'us.freeandfair.corla.asm.CountyDashboardASM', '50', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (15, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '50', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (16, 'us.freeandfair.corla.asm.CountyDashboardASM', '51', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (17, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '51', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (18, 'us.freeandfair.corla.asm.CountyDashboardASM', '52', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (19, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '52', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (20, 'us.freeandfair.corla.asm.CountyDashboardASM', '53', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (21, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '53', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (22, 'us.freeandfair.corla.asm.CountyDashboardASM', '10', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (23, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '10', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (24, 'us.freeandfair.corla.asm.CountyDashboardASM', '54', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (25, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '54', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (26, 'us.freeandfair.corla.asm.CountyDashboardASM', '11', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (27, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '11', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (28, 'us.freeandfair.corla.asm.CountyDashboardASM', '55', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (29, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '55', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (30, 'us.freeandfair.corla.asm.CountyDashboardASM', '12', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (31, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '12', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (32, 'us.freeandfair.corla.asm.CountyDashboardASM', '56', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (33, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '56', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (34, 'us.freeandfair.corla.asm.CountyDashboardASM', '13', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (35, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '13', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (36, 'us.freeandfair.corla.asm.CountyDashboardASM', '57', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (37, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '57', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (38, 'us.freeandfair.corla.asm.CountyDashboardASM', '14', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (39, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '14', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (40, 'us.freeandfair.corla.asm.CountyDashboardASM', '58', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (41, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '58', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (42, 'us.freeandfair.corla.asm.CountyDashboardASM', '15', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (43, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '15', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (44, 'us.freeandfair.corla.asm.CountyDashboardASM', '59', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (45, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '59', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (46, 'us.freeandfair.corla.asm.CountyDashboardASM', '16', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (47, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '16', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (48, 'us.freeandfair.corla.asm.CountyDashboardASM', '17', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (49, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '17', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (50, 'us.freeandfair.corla.asm.CountyDashboardASM', '18', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (51, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '18', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (52, 'us.freeandfair.corla.asm.CountyDashboardASM', '19', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (53, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '19', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (54, 'us.freeandfair.corla.asm.CountyDashboardASM', '1', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (55, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '1', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (57, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '2', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (59, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '3', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (60, 'us.freeandfair.corla.asm.CountyDashboardASM', '4', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (61, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '4', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (62, 'us.freeandfair.corla.asm.CountyDashboardASM', '5', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (63, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '5', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (64, 'us.freeandfair.corla.asm.CountyDashboardASM', '6', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (65, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '6', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (66, 'us.freeandfair.corla.asm.CountyDashboardASM', '7', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (67, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '7', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (68, 'us.freeandfair.corla.asm.CountyDashboardASM', '8', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (69, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '8', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (70, 'us.freeandfair.corla.asm.CountyDashboardASM', '9', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (71, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '9', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (72, 'us.freeandfair.corla.asm.CountyDashboardASM', '60', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (73, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '60', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (74, 'us.freeandfair.corla.asm.CountyDashboardASM', '61', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (75, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '61', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (76, 'us.freeandfair.corla.asm.CountyDashboardASM', '62', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (77, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '62', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (78, 'us.freeandfair.corla.asm.CountyDashboardASM', '63', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (79, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '63', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (80, 'us.freeandfair.corla.asm.CountyDashboardASM', '20', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (81, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '20', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (82, 'us.freeandfair.corla.asm.CountyDashboardASM', '64', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (83, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '64', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (84, 'us.freeandfair.corla.asm.CountyDashboardASM', '21', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (85, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '21', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (86, 'us.freeandfair.corla.asm.CountyDashboardASM', '22', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (87, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '22', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (88, 'us.freeandfair.corla.asm.CountyDashboardASM', '23', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (89, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '23', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (90, 'us.freeandfair.corla.asm.CountyDashboardASM', '24', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (91, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '24', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (92, 'us.freeandfair.corla.asm.CountyDashboardASM', '25', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (56, 'us.freeandfair.corla.asm.CountyDashboardASM', '2', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'BALLOT_MANIFEST_AND_CVRS_OK', 3);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (93, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '25', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (94, 'us.freeandfair.corla.asm.CountyDashboardASM', '26', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (95, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '26', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (96, 'us.freeandfair.corla.asm.CountyDashboardASM', '27', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (97, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '27', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (98, 'us.freeandfair.corla.asm.CountyDashboardASM', '28', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (99, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '28', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (100, 'us.freeandfair.corla.asm.CountyDashboardASM', '29', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (101, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '29', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (102, 'us.freeandfair.corla.asm.CountyDashboardASM', '30', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (103, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '30', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (104, 'us.freeandfair.corla.asm.CountyDashboardASM', '31', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (105, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '31', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (106, 'us.freeandfair.corla.asm.CountyDashboardASM', '32', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (107, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '32', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (108, 'us.freeandfair.corla.asm.CountyDashboardASM', '33', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (109, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '33', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (110, 'us.freeandfair.corla.asm.CountyDashboardASM', '34', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (111, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '34', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (112, 'us.freeandfair.corla.asm.CountyDashboardASM', '35', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (113, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '35', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (114, 'us.freeandfair.corla.asm.CountyDashboardASM', '36', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (115, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '36', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (116, 'us.freeandfair.corla.asm.CountyDashboardASM', '37', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (117, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '37', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (118, 'us.freeandfair.corla.asm.CountyDashboardASM', '38', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (119, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '38', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (120, 'us.freeandfair.corla.asm.CountyDashboardASM', '39', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (121, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '39', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (122, 'us.freeandfair.corla.asm.CountyDashboardASM', '40', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (123, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '40', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (124, 'us.freeandfair.corla.asm.CountyDashboardASM', '41', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (125, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '41', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (126, 'us.freeandfair.corla.asm.CountyDashboardASM', '42', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (127, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '42', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (128, 'us.freeandfair.corla.asm.CountyDashboardASM', '43', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'COUNTY_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (129, 'us.freeandfair.corla.asm.AuditBoardDashboardASM', '43', 'us.freeandfair.corla.asm.ASMState$AuditBoardDashboardState', 'AUDIT_INITIAL_STATE', 0);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (58, 'us.freeandfair.corla.asm.CountyDashboardASM', '3', 'us.freeandfair.corla.asm.ASMState$CountyDashboardState', 'BALLOT_MANIFEST_AND_CVRS_OK', 3);
INSERT INTO public.asm_state (id, asm_class, asm_identity, state_class, state_value, version) VALUES (1, 'us.freeandfair.corla.asm.DoSDashboardASM', 'DoS', 'us.freeandfair.corla.asm.ASMState$DoSDashboardState', 'COMPLETE_AUDIT_INFO_SET', 2);


--
-- Data for Name: assertion; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: assertion_context; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: assertion_discrepancies; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



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

INSERT INTO public.ballot_manifest_info (id, batch_id, batch_size, county_id, scanner_id, sequence_end, sequence_start, storage_location, version, ultimate_sequence_end, ultimate_sequence_start, uri) VALUES (204, '1', 10, 3, 1, 10, 1, 'Bin 1', 0, NULL, NULL, 'bmi:3:1-1');
INSERT INTO public.ballot_manifest_info (id, batch_id, batch_size, county_id, scanner_id, sequence_end, sequence_start, storage_location, version, ultimate_sequence_end, ultimate_sequence_start, uri) VALUES (266, '1', 10, 2, 1, 10, 1, 'Bin 1', 0, NULL, NULL, 'bmi:2:1-1');


--
-- Data for Name: cast_vote_record; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (177, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 1, '1-1-1', 1, 'UPLOADED', 1, 0, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-1');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (178, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 2, '1-1-2', 2, 'UPLOADED', 1, 1, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-2');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (179, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 3, '1-1-3', 3, 'UPLOADED', 1, 2, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-3');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (180, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 4, '1-1-4', 4, 'UPLOADED', 1, 3, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-4');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (181, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 5, '1-1-5', 5, 'UPLOADED', 1, 4, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-5');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (182, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 6, '1-1-6', 6, 'UPLOADED', 1, 5, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-6');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (183, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 7, '1-1-7', 7, 'UPLOADED', 1, 6, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-7');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (184, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 8, '1-1-8', 8, 'UPLOADED', 1, 7, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-8');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (185, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 9, '1-1-9', 9, 'UPLOADED', 1, 8, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-9');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (186, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 3, 10, '1-1-10', 10, 'UPLOADED', 1, 9, NULL, 0, NULL, NULL, NULL, 'cvr:3:1-1-10');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (243, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 1, '1-1-1', 1, 'UPLOADED', 1, 0, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-1');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (244, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 2, '1-1-2', 2, 'UPLOADED', 1, 1, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-2');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (245, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 3, '1-1-3', 3, 'UPLOADED', 1, 2, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-3');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (246, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 4, '1-1-4', 4, 'UPLOADED', 1, 3, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-4');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (247, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 5, '1-1-5', 5, 'UPLOADED', 1, 4, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-5');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (248, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 6, '1-1-6', 6, 'UPLOADED', 1, 5, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-6');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (249, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 7, '1-1-7', 7, 'UPLOADED', 1, 6, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-7');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (250, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 8, '1-1-8', 8, 'UPLOADED', 1, 7, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-8');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (251, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 9, '1-1-9', 9, 'UPLOADED', 1, 8, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-9');
INSERT INTO public.cast_vote_record (id, audit_board_index, comment, cvr_id, ballot_type, batch_id, county_id, cvr_number, imprinted_id, record_id, record_type, scanner_id, sequence_number, "timestamp", version, rand, revision, round_number, uri) VALUES (252, NULL, NULL, NULL, 'Ballot 1 - Type 1', '1', 2, 10, '1-1-10', 10, 'UPLOADED', 1, 9, NULL, 0, NULL, NULL, NULL, 'cvr:2:1-1-10');


--
-- Data for Name: comparison_audit; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: contest; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (171, 'IRV', 'TinyExample1', 0, 0, 3, 1, 3);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (173, 'PLURALITY', 'PluralityExample1', 1, 0, 1, 1, 3);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (241, 'IRV', 'TinyExample1', 0, 0, 3, 1, 2);
INSERT INTO public.contest (id, description, name, sequence_number, version, votes_allowed, winners_allowed, county_id) VALUES (175, 'PLURALITY', 'PluralityExample2', 2, 1, 2, 2, 3);


--
-- Data for Name: contest_choice; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (171, '', false, 'Alice', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (171, '', false, 'Bob', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (171, '', false, 'Chuan', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (173, '', false, 'Diego', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (173, '', false, 'Eli', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (173, '', false, 'Farhad', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (175, '', false, 'Gertrude', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (175, '', false, 'Ho', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241, '', false, 'Alice', false, 0);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241, '', false, 'Bob', false, 1);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (241, '', false, 'Chuan', false, 2);
INSERT INTO public.contest_choice (contest_id, description, fictitious, name, qualified_write_in, index) VALUES (175, '', false, 'Imogen ', false, 2);


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

INSERT INTO public.contest_to_audit (dashboard_id, audit, contest_id, reason) VALUES (0, 'COMPARISON', 241, 'COUNTY_WIDE_CONTEST');
INSERT INTO public.contest_to_audit (dashboard_id, audit, contest_id, reason) VALUES (0, 'COMPARISON', 173, 'COUNTY_WIDE_CONTEST');


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

INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (172, 10, 10, '["Alice","Chuan"]', 0, 0, 1, '["Bob"]', 1, 171, 3);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (174, 10, 10, '["Eli","Farhad"]', 1, 1, 1, '["Diego"]', 1, 173, 3);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (242, 10, 10, '["Alice","Chuan"]', 0, 0, 1, '["Bob"]', 1, 241, 2);
INSERT INTO public.county_contest_result (id, contest_ballot_count, county_ballot_count, losers, max_margin, min_margin, version, winners, winners_allowed, contest_id, county_id) VALUES (176, 10, 10, '["Imogen"]', 7, 6, 2, '["Ho","Gertrude"]', 2, 175, 3);


--
-- Data for Name: county_contest_vote_total; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (172, 10, 'Bob');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (172, 10, 'Alice');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (172, 10, 'Chuan');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (174, 3, 'Eli');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (174, 4, 'Diego');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (174, 3, 'Farhad');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (176, 8, 'Ho');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (176, 9, 'Gertrude');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (242, 10, 'Bob');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (242, 10, 'Alice');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (242, 10, 'Chuan');
INSERT INTO public.county_contest_vote_total (result_id, vote_total, choice) VALUES (176, 2, 'Imogen ');


--
-- Data for Name: county_dashboard; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (44, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.10538', 0, '{}', '{}', 0, 44, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (45, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.125187', 0, '{}', '{}', 0, 45, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (46, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.137228', 0, '{}', '{}', 0, 46, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (47, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.14776', 0, '{}', '{}', 0, 47, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (48, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.156459', 0, '{}', '{}', 0, 48, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (49, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.166044', 0, '{}', '{}', 0, 49, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (50, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.178636', 0, '{}', '{}', 0, 50, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (51, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.19176', 0, '{}', '{}', 0, 51, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (52, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.203765', 0, '{}', '{}', 0, 52, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (53, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.215124', 0, '{}', '{}', 0, 53, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (10, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.226466', 0, '{}', '{}', 0, 10, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (54, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.236983', 0, '{}', '{}', 0, 54, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (11, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.2473', 0, '{}', '{}', 0, 11, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (55, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.256508', 0, '{}', '{}', 0, 55, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (12, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.265603', 0, '{}', '{}', 0, 12, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (56, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.273577', 0, '{}', '{}', 0, 56, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (13, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.282363', 0, '{}', '{}', 0, 13, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (57, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.291136', 0, '{}', '{}', 0, 57, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (14, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.299811', 0, '{}', '{}', 0, 14, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (58, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.309304', 0, '{}', '{}', 0, 58, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (15, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.318691', 0, '{}', '{}', 0, 15, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (59, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.327655', 0, '{}', '{}', 0, 59, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (16, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.337235', 0, '{}', '{}', 0, 16, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (17, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.349464', 0, '{}', '{}', 0, 17, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (18, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.356924', 0, '{}', '{}', 0, 18, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (19, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.366692', 0, '{}', '{}', 0, 19, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (1, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.376125', 0, '{}', '{}', 0, 1, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (4, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.407183', 0, '{}', '{}', 0, 4, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (5, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.416869', 0, '{}', '{}', 0, 5, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (6, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.429023', 0, '{}', '{}', 0, 6, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (7, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.439448', 0, '{}', '{}', 0, 7, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (8, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.448578', 0, '{}', '{}', 0, 8, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (9, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.457445', 0, '{}', '{}', 0, 9, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (60, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.466181', 0, '{}', '{}', 0, 60, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (61, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.475496', 0, '{}', '{}', 0, 61, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (62, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.484986', 0, '{}', '{}', 0, 62, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (63, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.494268', 0, '{}', '{}', 0, 63, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (20, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.506778', 0, '{}', '{}', 0, 20, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (64, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.512767', 0, '{}', '{}', 0, 64, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (21, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.518522', 0, '{}', '{}', 0, 21, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (22, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.528445', 0, '{}', '{}', 0, 22, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (23, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.533801', 0, '{}', '{}', 0, 23, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (24, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.542615', 0, '{}', '{}', 0, 24, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (25, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.551046', 0, '{}', '{}', 0, 25, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (26, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.559948', 0, '{}', '{}', 0, 26, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (27, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.56855', 0, '{}', '{}', 0, 27, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (28, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.577831', 0, '{}', '{}', 0, 28, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (29, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.586524', 0, '{}', '{}', 0, 29, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (30, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.592124', 0, '{}', '{}', 0, 30, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (31, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.598271', 0, '{}', '{}', 0, 31, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (32, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.603867', 0, '{}', '{}', 0, 32, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (33, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.609305', 0, '{}', '{}', 0, 33, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (34, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.615298', 0, '{}', '{}', 0, 34, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (35, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.621309', 0, '{}', '{}', 0, 35, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (36, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.626866', 0, '{}', '{}', 0, 36, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (37, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.632446', 0, '{}', '{}', 0, 37, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (38, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.63892', 0, '{}', '{}', 0, 38, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (39, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.644544', 0, '{}', '{}', 0, 39, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (40, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.650262', 0, '{}', '{}', 0, 40, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (41, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.656229', 0, '{}', '{}', 0, 41, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (42, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.662979', 0, '{}', '{}', 0, 42, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (43, NULL, '[]', NULL, NULL, NULL, 0, 0, NULL, NULL, 'NOT_ATTEMPTED', '2024-06-17 16:54:29.669592', 0, '{}', '{}', 0, 43, NULL, NULL);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (3, NULL, '[]', NULL, NULL, NULL, 0, 10, NULL, NULL, 'SUCCESSFUL', '2024-06-17 16:55:48.990888', 10, '{}', '{}', 4, 3, 160, 199);
INSERT INTO public.county_dashboard (id, audit_board_count, driving_contests, audit_timestamp, audited_prefix_length, audited_sample_count, ballots_audited, ballots_in_manifest, current_round_index, cvr_import_error_message, cvr_import_state, cvr_import_timestamp, cvrs_imported, disagreements, discrepancies, version, county_id, cvr_file_id, manifest_file_id) VALUES (2, NULL, '[]', NULL, NULL, NULL, 0, 10, NULL, NULL, 'SUCCESSFUL', '2024-06-17 16:56:31.422751', 10, '{}', '{}', 4, 2, 230, 261);


--
-- Data for Name: county_dashboard_to_comparison_audit; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: cvr_audit_info; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: cvr_contest_info; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (177, 3, '["Alice","Bob","Chuan"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (177, 3, '["Diego"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (178, 3, '["Alice","Bob","Chuan"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (178, 3, '["Farhad"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (179, 3, '["Alice","Bob","Chuan"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (179, 3, '["Farhad"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (179, 3, '["Gertrude","Ho"]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (180, 3, '["Alice","Chuan","Bob"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (180, 3, '["Farhad"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (180, 3, '["Gertrude","Ho"]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (181, 3, '["Alice","Chuan","Bob"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (181, 3, '["Diego"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (181, 3, '["Gertrude","Ho"]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (182, 3, '["Alice","Chuan","Bob"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (182, 3, '["Diego"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (182, 3, '["Gertrude","Ho"]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (183, 3, '["Bob","Alice","Chuan"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (183, 3, '["Diego"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (183, 3, '["Gertrude","Ho"]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (184, 3, '["Chuan","Alice","Bob"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (184, 3, '["Eli"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (184, 3, '["Gertrude","Ho"]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (185, 3, '["Chuan","Alice","Bob"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (185, 3, '["Eli"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (185, 3, '["Gertrude","Ho"]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (186, 3, '["Chuan","Alice","Bob"]', NULL, NULL, 171, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (186, 3, '["Eli"]', NULL, NULL, 173, 1);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (186, 3, '["Gertrude","Ho"]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (243, 2, '["Alice","Bob","Chuan"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (244, 2, '["Alice","Bob","Chuan"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (245, 2, '["Alice","Bob","Chuan"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (246, 2, '["Alice","Chuan","Bob"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (247, 2, '["Alice","Chuan","Bob"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (248, 2, '["Alice","Chuan","Bob"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (249, 2, '["Bob","Alice","Chuan"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (250, 2, '["Chuan","Alice","Bob"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (251, 2, '["Chuan","Alice","Bob"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (252, 2, '["Chuan","Alice","Bob"]', NULL, NULL, 241, 0);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (177, 3, '["Gertrude","Imogen "]', NULL, NULL, 175, 2);
INSERT INTO public.cvr_contest_info (cvr_id, county_id, choices, comment, consensus, contest_id, index) VALUES (178, 3, '["Imogen "]', NULL, NULL, 175, 2);


--
-- Data for Name: dos_dashboard; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.dos_dashboard (id, canonical_choices, canonical_contests, election_date, election_type, public_meeting_date, risk_limit, seed, version) VALUES (0, '{"PluralityExample1":["Eli","Diego","Farhad"],"PluralityExample2":["","Ho","Imogen ","Gertrude"],"TinyExample1":["Bob","Alice","Chuan"]}', '{"Alamosa":["TinyExample1"],"Arapahoe":["PluralityExample1","PluralityExample2","TinyExample1"]}', '2024-06-17 16:56:49.331', 'general', '2024-06-24 16:56:52.478', 0.03000000, '2343245789234578923457892345789', 3);


--
-- Data for Name: log; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (130, '(unauthenticated)', 'localhost:8888', '7C65E0E26F69B5BA6BC86E4F7DF42B9634951AC9FB17E79D7EC1AE53019CF4AB', '/unauthenticate', 200, '2024-06-17 16:54:52.397211', 0, NULL);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (131, '(unauthenticated)', 'localhost:8888', 'DDEB03D43E1418F628A671A5745E5921224620B1E5645EC52F225F8C4F194CA8', 'unauthorized access on /dos-dashboard: client not authorized to perform this action', 401, '2024-06-17 16:54:55.945455', 0, 130);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (132, '(unauthenticated)', 'localhost:8888', '6DEAB4E73D5A743FA06617AC59323C873F138A144CF52E575310EF7C56FFE2F7', 'unauthorized access on /upload-file: client not authorized to perform this action', 401, '2024-06-17 16:55:05.970745', 0, 131);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (133, '(unauthenticated)', 'localhost:8888', 'F0F787E0A7B5A79785C912C1370C5C07185EEDE57B1729A409D89E934478859A', 'unauthorized access on /upload-file: client not authorized to perform this action', 401, '2024-06-17 16:55:06.873626', 0, 132);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (134, '(unauthenticated)', 'localhost:8888', '4470A9B32BD79223945EEB5585DDCE22E13442D42DE40551AFBB397BA1C70979', '/unauthenticate', 200, '2024-06-17 16:55:17.847808', 0, 133);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (135, '(unauthenticated)', 'localhost:8888', '33E939A47083E2F52573110E7DD2BB10A03DEAF78FA8C914B83816DF26C86F14', 'unauthorized access on /county-dashboard: client not authorized to perform this action', 401, '2024-06-17 16:55:20.505602', 0, 134);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (136, '(unauthenticated)', 'localhost:8888', 'A540442E91B0CE0A9E81CA75AAD7310CF42C3B8B6565D42927C354BAEB9E0B7A', 'unauthorized access on /audit-board-asm-state: client not authorized to perform this action', 401, '2024-06-17 16:55:20.505962', 0, 134);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (137, '(unauthenticated)', 'localhost:8888', 'B01D0E7E24207D5A25872C102F638781B4060D6B200A9801F166B9286ACAB3D3', 'unauthorized access on /county-asm-state: client not authorized to perform this action', 401, '2024-06-17 16:55:20.51111', 0, 136);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (138, 'countyadmin3', 'localhost:8888', '5012E26AE79FF978823486BA8473CF27F2800CF07FE99814FB6F009836E76932', '/auth-admin', 200, '2024-06-17 16:55:25.135085', 0, 137);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (139, 'countyadmin3', 'localhost:8888', 'EA723960F1523297E9FEE014E8957F2F1365D08EC235AAEEE957C39BC46D4D7B', '/auth-admin', 200, '2024-06-17 16:55:26.262488', 0, 138);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (140, 'countyadmin3', 'localhost:8888', 'A61438A274DE65B546AF3578DB514C3231D5A3B19DDB9D739CA13496A860D3D8', '/county-asm-state', 200, '2024-06-17 16:55:26.304886', 0, 139);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (141, 'countyadmin3', 'localhost:8888', '334682D01AC6D2B0762166BCC628C42739DED5A2AAB0CE72627F17714527D8F7', '/audit-board-asm-state', 200, '2024-06-17 16:55:26.304889', 0, 139);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (142, 'countyadmin3', 'localhost:8888', '847D422A2614A8A6DDBEC7DBC086419D0A60A0933226FE7460F69D92016A2320', '/county-dashboard', 200, '2024-06-17 16:55:26.326544', 0, 139);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (143, 'countyadmin3', 'localhost:8888', '31E78EF62CF845D3A3C56207E7CE852A00B615195438FFCC0704A8E76340371E', '/contest/county', 200, '2024-06-17 16:55:26.374219', 0, 142);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (144, 'countyadmin3', 'localhost:8888', '2EA20B4D5C69A87DD8747C8ECB837CC0A592555F3F4EDEF8067C9D5072DF7975', '/audit-board-asm-state', 200, '2024-06-17 16:55:31.37313', 0, 143);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (145, 'countyadmin3', 'localhost:8888', '0DB80800618B02D065689B0B130BF072B4F573923F126B3D533AC127DD913D07', '/county-asm-state', 200, '2024-06-17 16:55:31.373645', 0, 143);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (146, 'countyadmin3', 'localhost:8888', 'C7E29C6F5AD16AEA3AF262AE3F27AFE082DA7FE964B9B1E144A1A770EDF64931', '/county-dashboard', 200, '2024-06-17 16:55:31.379728', 0, 143);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (147, 'countyadmin3', 'localhost:8888', 'F17FB6243A29786BB038CD8D3A7CAA450CC1A3A37F7222FB90CC7A63E47EBB6E', '/contest/county', 200, '2024-06-17 16:55:31.419989', 0, 146);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (148, 'countyadmin3', 'localhost:8888', '567AE87C7EC8FB71FA5BC2EDCE9BD40512F6003F7CD32AF824E04A254F095891', '/audit-board-asm-state', 200, '2024-06-17 16:55:36.7037', 0, 147);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (149, 'countyadmin3', 'localhost:8888', '82BE5C080FB310128480C9DB95FE3874B64B972F271C098D908F158BCC1518D4', '/county-asm-state', 200, '2024-06-17 16:55:36.703508', 0, 147);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (150, 'countyadmin3', 'localhost:8888', '8ED69BE27720A6A5452F8E9D1149C26A3D95C54D5F552E0964090384C981E9A6', '/county-dashboard', 200, '2024-06-17 16:55:36.711349', 0, 147);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (151, 'countyadmin3', 'localhost:8888', 'BDC1BF761ACA83AEC2965899A146BBBDD8C3DC2E73E87D32B3CF17AC597780EC', '/contest/county', 200, '2024-06-17 16:55:36.747108', 0, 150);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (153, 'countyadmin3', 'localhost:8888', '28C1C0F60C1B33885C4B0910E650A0F592298FF0D633FB5ABB37F5F70461D190', '/county-asm-state', 200, '2024-06-17 16:55:41.748455', 0, 151);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (152, 'countyadmin3', 'localhost:8888', '5C2999A0FBA10BA643E1B6F55F0C194CB47C7F0B20583FD4BFB20360000B83F2', '/audit-board-asm-state', 200, '2024-06-17 16:55:41.748487', 0, 151);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (154, 'countyadmin3', 'localhost:8888', 'D394513DFEDDB046F2768CF2E6653309157AE1F4F0FA2828F34F559D11E96A1A', '/county-dashboard', 200, '2024-06-17 16:55:41.758111', 0, 151);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (155, 'countyadmin3', 'localhost:8888', '18087AD3D0F005C6217A2B26FAFCBED131515652207F46FFC2A5225DD0737FC2', '/contest/county', 200, '2024-06-17 16:55:41.792967', 0, 154);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (156, 'countyadmin3', 'localhost:8888', '41D7C09D12B15150DC3CD20963F8A6AEA9FF168961933A656793435171960306', '/county-asm-state', 200, '2024-06-17 16:55:46.793203', 0, 155);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (157, 'countyadmin3', 'localhost:8888', '31D1DC5E90E5CDAA806AF32D4BC53C40EC6CC21F3B06153FA6797E5B7D2A89E1', '/audit-board-asm-state', 200, '2024-06-17 16:55:46.79308', 0, 155);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (158, 'countyadmin3', 'localhost:8888', '432346944AD56DF58AB3C7F0365A968BF57373AD79D4D48617BB3F17F69FC4BB', '/county-dashboard', 200, '2024-06-17 16:55:46.801133', 0, 155);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (159, 'countyadmin3', 'localhost:8888', 'C90AF9220662648A9BF2E00418DDA0CEF734FB02505B6E9478252910583BB133', '/contest/county', 200, '2024-06-17 16:55:46.81987', 0, 158);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (161, 'countyadmin3', 'localhost:8888', '592AEF49DCEF973D6F3780DC9E41F987CDE5C497CF641377573CAB2E007E5E87', '/upload-file', 200, '2024-06-17 16:55:47.846594', 0, 159);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (162, 'countyadmin3', 'localhost:8888', '052AF213BA8D0C5FAB78FAC5C60565B8AEAF01614022FD5388B8E2C90414A8FD', '/audit-board-asm-state', 200, '2024-06-17 16:55:47.869568', 0, 161);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (163, 'countyadmin3', 'localhost:8888', 'B1DD3A774C0B8C1F6ACAF2E28FA54120048F1F97D8C3BCC7555CC9BCBC432053', '/county-asm-state', 200, '2024-06-17 16:55:47.870431', 0, 161);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (164, 'countyadmin3', 'localhost:8888', 'B21C15B3CD6B5F5EA4703F28E4B1ABFC18FE79A2600DDFA3650CCE38C459FB87', '/county-dashboard', 200, '2024-06-17 16:55:47.876841', 0, 161);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (165, 'countyadmin3', 'localhost:8888', 'D5D4F803AA0AD9C47FEB3D5DDF81F0CA0884BCA90C82041DD9BD714B37D25ABA', '/import-cvr-export', 200, '2024-06-17 16:55:47.889202', 0, 161);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (166, 'countyadmin3', 'localhost:8888', 'C5E1FCA1E4E3CFD2FEAFA6E15E9BFA1A773083CCEF551C295B64D5E374727DBB', '/contest/county', 200, '2024-06-17 16:55:47.892033', 0, 164);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (168, 'countyadmin3', 'localhost:8888', '8AAC17028AF409253941021B9305DD86C79FC0DB940D99DB0CDC71F42B843BF0', '/audit-board-asm-state', 200, '2024-06-17 16:55:47.905221', 0, 166);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (167, 'countyadmin3', 'localhost:8888', '72425B0FFE1E2CBD90872194B1199EE9DAB1CB9C2C8A780EF9F87E68584E959C', '/county-asm-state', 200, '2024-06-17 16:55:47.905221', 0, 166);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (169, 'countyadmin3', 'localhost:8888', '62A2F98931EE9C191CF0D5A0F0CB25D3D34F379ABCB43700871B25972EC69AF0', '/county-dashboard', 200, '2024-06-17 16:55:47.911755', 0, 166);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (170, 'countyadmin3', 'localhost:8888', 'E800E35ED285BFA094C7C95A235657E548483270CFCE602FE3DABF43CC6D7827', '/contest/county', 200, '2024-06-17 16:55:47.924084', 0, 169);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (187, 'countyadmin3', 'localhost:8888', 'F071C7E9D8DCC473D1FF6A641E1946E875493203449977B73A482EBD4FEDA7D1', '/audit-board-asm-state', 200, '2024-06-17 16:55:51.81917', 0, 170);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (188, 'countyadmin3', 'localhost:8888', 'B9CC2C470EF3E6FF13FFB37B6855A5093B0695DA8EF71C58D9AC02C356609453', '/county-asm-state', 200, '2024-06-17 16:55:51.819411', 0, 170);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (189, 'countyadmin3', 'localhost:8888', '814F64CFF5E55D14D6FCF2C51015CB447F45D8805B45C2BEB52ABF71E4F1B7C4', '/county-dashboard', 200, '2024-06-17 16:55:51.824936', 0, 170);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (190, 'countyadmin3', 'localhost:8888', 'F9156399903104C245B587A0CBDE48EAB39A0215E306D5B1804173CD4E4AA835', '/contest/county', 200, '2024-06-17 16:55:51.84746', 0, 189);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (191, 'countyadmin3', 'localhost:8888', 'A67E00F79714CB7CDD2DEB18FECDA87E7836324D625D70BD20C506C9D266E65C', '/audit-board-asm-state', 200, '2024-06-17 16:55:56.84976', 0, 190);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (196, 'countyadmin3', 'localhost:8888', '1529F1EEF89FBA851F1F829E1D145857E8D01A3E43EDB507C840B7951347A1FE', '/county-asm-state', 200, '2024-06-17 16:56:01.879313', 0, 194);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (201, 'countyadmin3', 'localhost:8888', '59A39A4266E44279F8B80C1E74B866178AE561675EB6739606234243FE3E309A', '/audit-board-asm-state', 200, '2024-06-17 16:56:04.199919', 0, 200);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (192, 'countyadmin3', 'localhost:8888', 'EE58F90BC87B13FF8D5A12B321053463979924597A59F48D6CB6D702F9A8A616', '/county-asm-state', 200, '2024-06-17 16:55:56.849832', 0, 190);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (195, 'countyadmin3', 'localhost:8888', 'A29361841C5A61655D057225030FB5BC528C6BC6417B6FAE2A6FBB7BCC26552A', '/audit-board-asm-state', 200, '2024-06-17 16:56:01.879074', 0, 194);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (203, 'countyadmin3', 'localhost:8888', '237F1B1AE7DA3CD97C0C2F5F5D8F43BC5FDB4ACE9C46267DCA24A210D1178D01', '/county-dashboard', 200, '2024-06-17 16:56:04.209165', 0, 200);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (206, 'countyadmin3', 'localhost:8888', '5665FE4CC3971C663017E3FFF7FA80B0A01762E1773278219D852A2B9F0ABB0F', '/contest/county', 200, '2024-06-17 16:56:04.226915', 0, 203);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (207, 'countyadmin3', 'localhost:8888', '26B127CE2C58DFC639D3DDEB15A11AF0704FC65FEA55355B22A344BF72A74659', '/county-asm-state', 200, '2024-06-17 16:56:04.233994', 0, 206);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (211, 'countyadmin3', 'localhost:8888', '3FBF7186AC982C078A19F0787E89C2C43B178FEE101D73F1ACED9F4E8763118E', '/audit-board-asm-state', 200, '2024-06-17 16:56:06.910447', 0, 210);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (218, 'countyadmin2', 'localhost:8888', '28FC55B4376560E00E0BEFA818462C2B9B260DD4A9DE2FDC55FC9D7E8DBD7DAB', '/county-asm-state', 200, '2024-06-17 16:56:16.574989', 0, 217);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (223, 'countyadmin2', 'localhost:8888', 'F550C7441A40CE018884582834A396A7B3887188E7CA7440B9D47173CFE34567', '/county-asm-state', 200, '2024-06-17 16:56:21.632547', 0, 221);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (227, 'countyadmin2', 'localhost:8888', '8610813DA5CFC6FE8AE46F4816086ACB1FCA5658E6D0A0FEE2CEBE17A2788111', '/audit-board-asm-state', 200, '2024-06-17 16:56:26.682428', 0, 225);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (193, 'countyadmin3', 'localhost:8888', '3CC64A567922A79F97EE3E23E16DCD953D1A11DCDCA60F035394045ADA8D7815', '/county-dashboard', 200, '2024-06-17 16:55:56.856249', 0, 190);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (194, 'countyadmin3', 'localhost:8888', '5995B584942D2EC0B79C570B817758C6F2A88CC6206C57431732299499EBD28F', '/contest/county', 200, '2024-06-17 16:55:56.877549', 0, 193);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (197, 'countyadmin3', 'localhost:8888', '25009066EAF829AFEBD0DCECA9B7973D8AA4E9F1589AB8F66161EEC29D59B004', '/county-dashboard', 200, '2024-06-17 16:56:01.888374', 0, 194);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (198, 'countyadmin3', 'localhost:8888', 'D403B54D19E9F589B047CD2A526B8CAF888C41F1E2E285F58526E06CBDCDCE1B', '/contest/county', 200, '2024-06-17 16:56:01.904167', 0, 197);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (200, 'countyadmin3', 'localhost:8888', '779443F073C4CA6D31486898A2144621101632FB439FC55BD0624303DD492947', '/upload-file', 200, '2024-06-17 16:56:04.181851', 0, 198);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (202, 'countyadmin3', 'localhost:8888', '2BF519A8F373E1FC579A50C4A0FFB86C925F2366278C19033CAD52A6473D5B11', '/county-asm-state', 200, '2024-06-17 16:56:04.20001', 0, 200);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (209, 'countyadmin3', 'localhost:8888', '8392C3757956427314D2883CB6CDB8FD7ED60E84065B33374FC4A5D36985539A', '/county-dashboard', 200, '2024-06-17 16:56:04.243587', 0, 206);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (210, 'countyadmin3', 'localhost:8888', '4DDD8D479EB2A39C311F65265B41E376CC288FCFE9E8A84B237C5E9AB836A475', '/contest/county', 200, '2024-06-17 16:56:04.263429', 0, 209);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (212, 'countyadmin3', 'localhost:8888', '05D276FC5088076207FCECB1B02E8EE783A1905979DFDF90B4236816D12A2B4B', '/county-asm-state', 200, '2024-06-17 16:56:06.910439', 0, 210);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (219, 'countyadmin2', 'localhost:8888', '1E06BFBA9B5A62CA5176DB15815ADB7589CB0E5D15798160B92FFC2438359058', '/audit-board-asm-state', 200, '2024-06-17 16:56:16.574991', 0, 217);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (224, 'countyadmin2', 'localhost:8888', 'F8D4CC5C0681EA919BBFA1D38615901B5FB67DC6EE59AF84EE20CA34689EDEB9', '/county-dashboard', 200, '2024-06-17 16:56:21.639251', 0, 221);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (225, 'countyadmin2', 'localhost:8888', '97EB7C756C34274359C6CE2524E18C5A006A071230BBA4753F0EE18DF3F62742', '/contest/county', 200, '2024-06-17 16:56:21.677801', 0, 224);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (228, 'countyadmin2', 'localhost:8888', '295CAC8F64B7248531D98BD1B9C351307F9380669D617B9FB8672E350D0D7867', '/county-dashboard', 200, '2024-06-17 16:56:26.688348', 0, 225);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (229, 'countyadmin2', 'localhost:8888', '35B5EFF673023AD91A156467ED6E3A326E30B1323D12C03C0D95AED4E93FD502', '/contest/county', 200, '2024-06-17 16:56:26.719247', 0, 228);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (205, 'countyadmin3', 'localhost:8888', 'BCCA0F683B2A5A1D5D0EAEA693A22516AE4607068930CFC72070D0F7F999674E', '/import-ballot-manifest', 200, '2024-06-17 16:56:04.218094', 0, 200);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (208, 'countyadmin3', 'localhost:8888', 'EB92B1A1A587DA043FC0BFFDF64F4F67182396DA91B1FD0034EED5CBDEA89FDB', '/audit-board-asm-state', 200, '2024-06-17 16:56:04.233988', 0, 206);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (213, 'countyadmin3', 'localhost:8888', 'BE562BAA2F5F6445ADD2C72C644F63C3B930CCC9D75A5DA4DD9DFD6129A836C3', '/county-dashboard', 200, '2024-06-17 16:56:06.921739', 0, 210);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (214, 'countyadmin3', 'localhost:8888', 'D56B60997C786092EFBE207007DB9B0684D9BECC61CEA48FFF5F2E16B659D947', '/contest/county', 200, '2024-06-17 16:56:06.941624', 0, 213);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (215, '(unauthenticated)', 'localhost:8888', '65E150EF41C3BE5E4157F56AF5CA148764965AF9998FBB1876C30F0ABC7A9A1C', '/unauthenticate', 200, '2024-06-17 16:56:07.549841', 0, 214);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (216, 'countyadmin2', 'localhost:8888', '38FB46C33879F7B4BDBB5227D47405F1DADB025B1AD7D655B890296D822613D2', '/auth-admin', 200, '2024-06-17 16:56:14.956741', 0, 215);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (217, 'countyadmin2', 'localhost:8888', '383451E28B6A133EBCF5C9A5927329071F78CB277E32961EFD58605EE5ADC96D', '/auth-admin', 200, '2024-06-17 16:56:16.537525', 0, 216);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (220, 'countyadmin2', 'localhost:8888', '5D8522E68AD2908E09029355805EC9A4900A6BF3EF6872C2EB2925D0EA4EC85D', '/county-dashboard', 200, '2024-06-17 16:56:16.581752', 0, 217);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (221, 'countyadmin2', 'localhost:8888', '7F372F8031C029E74CB6812F74FC47827C264CC33C2E8E52632243355B0877BF', '/contest/county', 200, '2024-06-17 16:56:16.624197', 0, 220);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (222, 'countyadmin2', 'localhost:8888', 'FBC02C8BCD104CF54D4DCCCA252DB2401923CDD60A234E2A531A2315841C0F80', '/audit-board-asm-state', 200, '2024-06-17 16:56:21.63238', 0, 221);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (226, 'countyadmin2', 'localhost:8888', 'EF11578B238C0380C66C08876ACDAB7AC34E7943928057EE826C4F0C5887803C', '/county-asm-state', 200, '2024-06-17 16:56:26.682011', 0, 225);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (231, 'countyadmin2', 'localhost:8888', '4EA21749C3B6134C0ADA64B3BE29B9C4FC522AE770DAE0200992BCF8B96CA0B4', '/upload-file', 200, '2024-06-17 16:56:30.360356', 0, 229);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (232, 'countyadmin2', 'localhost:8888', 'C2F77D111EC6B0195A30FBBB4CCBCFA0BF251368B53ED708EB549E7E9B218BE3', '/audit-board-asm-state', 200, '2024-06-17 16:56:30.381843', 0, 231);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (233, 'countyadmin2', 'localhost:8888', '9EC04F294EBD69804621812BD9074C35E7AC7576DC524636EF3DD49749F03883', '/county-asm-state', 200, '2024-06-17 16:56:30.381843', 0, 231);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (235, 'countyadmin2', 'localhost:8888', 'B190CDAFFC6E42ED95179450307FA94623DFF43C8B34A0C9EBB4ACCDB841E6D9', '/county-dashboard', 200, '2024-06-17 16:56:30.387997', 0, 231);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (234, 'countyadmin2', 'localhost:8888', '3307A65C9BF83B8B2A02DA9F0EBB160A0047AE9063CB3F77D1817F7488A97977', '/import-cvr-export', 200, '2024-06-17 16:56:30.386357', 0, 231);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (236, 'countyadmin2', 'localhost:8888', '861B5BBDF6E4F7AE8F281237687B666C548F2E483E2C73A34D3EE71638FDD727', '/contest/county', 200, '2024-06-17 16:56:30.404274', 0, 235);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (237, 'countyadmin2', 'localhost:8888', 'B18E2F92FD9A2F181A48744032B04F963575FA0EB30DEB847F8AD870CC5C7053', '/audit-board-asm-state', 200, '2024-06-17 16:56:30.40771', 0, 235);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (238, 'countyadmin2', 'localhost:8888', '8B8435E3D4D77C2EAC266CA37AB44FCFD755EBA6BE92BD335524961D151A146D', '/county-asm-state', 200, '2024-06-17 16:56:30.407884', 0, 235);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (239, 'countyadmin2', 'localhost:8888', 'E4F0E3C64240A43C9F9ADFC6DCDBF2F5E88618F682925CC1057C8435C1BC7946', '/county-dashboard', 200, '2024-06-17 16:56:30.414401', 0, 236);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (240, 'countyadmin2', 'localhost:8888', '31FF24F7E1CC2DA60655EA6C34F91830F14DF21E117F97891850F0156AF253BB', '/contest/county', 200, '2024-06-17 16:56:30.427444', 0, 239);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (253, 'countyadmin2', 'localhost:8888', '24772F7A41057E52E77FC0501033C6D79EB269DF7BFE4188EA4E68DC34FB68AF', '/audit-board-asm-state', 200, '2024-06-17 16:56:31.720795', 0, 240);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (254, 'countyadmin2', 'localhost:8888', 'EB06D2B48A1DD97E6D2734862BDF6EDA24B0A7F7B9604396BF31E04F0BD8454E', '/county-asm-state', 200, '2024-06-17 16:56:31.721074', 0, 240);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (255, 'countyadmin2', 'localhost:8888', '8E647FD9C8FB85718FC4A599CF43FE34E88812576E29FD3524EE3CA11D35A46F', '/county-dashboard', 200, '2024-06-17 16:56:31.727545', 0, 240);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (256, 'countyadmin2', 'localhost:8888', '1DFBA82D2ABB1E3D74C365F9157B09F9403C6B8E336410C03CBC79FFD2960A39', '/contest/county', 200, '2024-06-17 16:56:31.762115', 0, 255);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (257, 'countyadmin2', 'localhost:8888', '6C66B979DB9E4F1E662EA171D2BA8EF90A792CA5C4CC514B1D79AA7A16CFE589', '/audit-board-asm-state', 200, '2024-06-17 16:56:36.768113', 0, 256);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (258, 'countyadmin2', 'localhost:8888', '53AA0E791E59A59BCDCDA629A472E0CAA3E3DF72724731DDF1C4E0BD69DE664C', '/county-asm-state', 200, '2024-06-17 16:56:36.768248', 0, 256);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (259, 'countyadmin2', 'localhost:8888', 'A4594534A72E0842F77410770FF15C39F37BF80A055CBE1FC7FB38691F0113D7', '/county-dashboard', 200, '2024-06-17 16:56:36.773985', 0, 256);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (260, 'countyadmin2', 'localhost:8888', 'B340DACFEE30FFB37FCFBCCF897BC7498289CC2E11BF147E88F6CBDE7D251DC8', '/contest/county', 200, '2024-06-17 16:56:36.800537', 0, 259);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (262, 'countyadmin2', 'localhost:8888', '6A6CED8A332B610A604253670E28B19C1B6BF32DB7348EDE4D13CBB0F1242717', '/upload-file', 200, '2024-06-17 16:56:41.031788', 0, 260);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (263, 'countyadmin2', 'localhost:8888', 'ACF224C68E982A1F445A0D9D5DD8AC5621C41409BABDB720F9F4CE4C371CA2A8', '/county-asm-state', 200, '2024-06-17 16:56:41.047543', 0, 262);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (264, 'countyadmin2', 'localhost:8888', '8602446FD35F6D7AED4ACCD6D147229BFC0F7535D739C69BC0B1BEED1531BCD0', '/audit-board-asm-state', 200, '2024-06-17 16:56:41.047668', 0, 262);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (265, 'countyadmin2', 'localhost:8888', '6301A6BB5F7E2758D84EF313DE60542781E7CDE894085BC344BDDEA0BC899DF8', '/county-dashboard', 200, '2024-06-17 16:56:41.054616', 0, 262);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (267, 'countyadmin2', 'localhost:8888', 'EA4F8609DD45FCE6B63EFDD233D344C832163577D5A9A2389EFCCC9A4C437599', '/import-ballot-manifest', 200, '2024-06-17 16:56:41.059593', 0, 262);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (268, 'countyadmin2', 'localhost:8888', '71BE5881B56B97A59A91D80D01421CCE40E767B9E4C95283868EBB052CC26946', '/contest/county', 200, '2024-06-17 16:56:41.075249', 0, 267);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (269, 'countyadmin2', 'localhost:8888', '1A17EF61FE3D1F4CCB6F12E6102BA093C125E38B04E20DB09A091FB41853E184', '/county-asm-state', 200, '2024-06-17 16:56:41.077433', 0, 267);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (270, 'countyadmin2', 'localhost:8888', 'E6ECEFA26CEFCAE6393822D4FDB2F184305761BB6C19C6B14646C0021059CCD7', '/audit-board-asm-state', 200, '2024-06-17 16:56:41.082033', 0, 269);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (271, 'countyadmin2', 'localhost:8888', '5750D6CC1226F3D0A43A080EB46F0C412B609EC9E53179B0FB7B76A9600FF3BF', '/county-dashboard', 200, '2024-06-17 16:56:41.087137', 0, 267);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (272, 'countyadmin2', 'localhost:8888', '01013F0AB818AF5AC47BE999AA9E54CF813E1362AD1B90CA0208A33EA6048E58', '/contest/county', 200, '2024-06-17 16:56:41.116816', 0, 271);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (273, 'countyadmin2', 'localhost:8888', 'CCA3FF9C33AD420AB09CF111ED8CF96E2FB3505A233E450FD7E8B3829F9F0038', '/county-asm-state', 200, '2024-06-17 16:56:41.802982', 0, 272);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (274, 'countyadmin2', 'localhost:8888', 'C73F904354037B2CAF1853AC31899A793B2DCF87841C0B6FAE5C359EE41C4755', '/audit-board-asm-state', 200, '2024-06-17 16:56:41.803019', 0, 272);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (275, 'countyadmin2', 'localhost:8888', '1C41CED26AF9AADC693DA14820DFD68F444B720DB023570AA8FF2F9106C9F4D1', '/county-dashboard', 200, '2024-06-17 16:56:41.810998', 0, 272);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (276, 'countyadmin2', 'localhost:8888', '7531DB8A607976742715B47A039ECE16F5519318104B9DED9B1111F317759F31', '/contest/county', 200, '2024-06-17 16:56:41.828529', 0, 275);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (277, 'countyadmin2', 'localhost:8888', 'A1B0F8E0EB37EB731A021F2B2A1994691DD1F89906464990827D0ED0C7EF2D8B', '/county-asm-state', 200, '2024-06-17 16:56:46.828906', 0, 276);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (278, 'countyadmin2', 'localhost:8888', '9D8F784E6EA016D7CDA4149C74DF18D41CEB701A71CE1993889D473BB126DB45', '/audit-board-asm-state', 200, '2024-06-17 16:56:46.828894', 0, 276);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (279, 'countyadmin2', 'localhost:8888', '0144F5C4C37066D5B0574E087B50CD7288787A0D4C93D18FB505023896D3A126', '/county-dashboard', 200, '2024-06-17 16:56:46.835727', 0, 276);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (280, 'countyadmin2', 'localhost:8888', 'C4F5481935398F626BF5965EC8BFE3D2E673ED9E97B1D78DD8E769BE18834665', '/contest/county', 200, '2024-06-17 16:56:46.852018', 0, 279);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (281, 'stateadmin1', 'localhost:8888', '77D4275151DE52D3CFADAD66DD0A5498F6B62D5FD780870B7A86F34CFF9CD88D', '/auth-admin', 200, '2024-06-17 16:56:48.303987', 0, 280);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (282, 'stateadmin1', 'localhost:8888', '5A3CA95F5C6D0CC684B668723E4C4A5337FAE7E0F129E81FF9C8442F46FFFD2A', '/auth-admin', 200, '2024-06-17 16:56:49.078864', 0, 281);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (283, 'stateadmin1', 'localhost:8888', 'A73EB10AB7D9C942941B2224AB6F39CD4A2958B41ED792DB32D8E50F5B876DBB', '/dos-dashboard', 200, '2024-06-17 16:56:49.323016', 0, 282);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (284, 'stateadmin1', 'localhost:8888', '5D13E596ED35E7E55F5C3F70C15ED73AB172C02A161187F05F11119763607FF4', '/contest', 200, '2024-06-17 16:56:49.395806', 0, 283);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (287, 'countyadmin2', 'localhost:8888', '9A0DC7ABBBEFD078BE98667F4D48C36F829DF5E7DB076EF608FE7F39CC6865AD', '/county-dashboard', 200, '2024-06-17 16:56:51.859885', 0, 284);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (288, 'countyadmin2', 'localhost:8888', '6DAA1A5A3A0906016AC0A64F3D524875E0392AD50C4C4A41788F430340B874DE', '/contest/county', 200, '2024-06-17 16:56:51.876131', 0, 287);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (289, 'stateadmin1', 'localhost:8888', 'AB7E9474943ED19A26892C9C7F16AA6AAD45B944F5A39E8E6B7CB1530920FFA0', '/dos-dashboard', 200, '2024-06-17 16:56:52.639521', 0, 288);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (291, 'countyadmin2', 'localhost:8888', '5F38A5B0B94B5CA8FF20DC15D531E111BAEB54932EEB7960176516A50510553E', '/county-asm-state', 200, '2024-06-17 16:56:56.876411', 0, 289);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (296, 'stateadmin1', 'localhost:8888', '986FAB1BD1919720B9E1DE4C9F8981BEA8DA166DBC5AD0A74EA044728C302E34', '/dos-dashboard', 200, '2024-06-17 16:57:00.30481', 0, 293);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (297, 'stateadmin1', 'localhost:8888', '2A7F85454066326B18A6D4077EA7C298040198490FA8C72AB9FCD7FF9371775E', '/contest', 200, '2024-06-17 16:57:00.322976', 0, 296);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (303, 'countyadmin2', 'localhost:8888', 'AFDF7F62B057DA03D544ECF9856F18104C571B314B3B533EFB3DD1E772E09631', '/county-asm-state', 200, '2024-06-17 16:57:01.898386', 0, 301);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (305, 'countyadmin2', 'localhost:8888', 'E28A12F2FAA6FD002C090FBA94B74A0D7BB89941703C47FB084C943FB0C8A802', '/contest/county', 200, '2024-06-17 16:57:01.926931', 0, 304);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (310, 'stateadmin1', 'localhost:8888', 'FA1BF1A01B1937DC85E32870EF2AFF73E853976A8583A46E079706F9B8F3A2B2', '/dos-dashboard', 200, '2024-06-17 16:57:05.028705', 0, 307);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (313, 'countyadmin2', 'localhost:8888', 'D2FE015B6D51E4AF9370A47F3683556733DC2F0560E7FE9CAFCE6AC8CF3B70BC', '/county-asm-state', 200, '2024-06-17 16:57:06.927313', 0, 311);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (318, 'stateadmin1', 'localhost:8888', 'BBCEE71C8096268DBE611EA917DFA53933EEAF31C5E67186CF10CC2A11C8A1A2', '/dos-dashboard', 200, '2024-06-17 16:57:09.334235', 0, 315);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (405, 'countyadmin2', 'localhost:8888', '9C63C017BFCAC392D2F36DFED8552F5A7EFCA1F6F99072CAD56A1D9E61420994', '/audit-board-asm-state', 200, '2024-06-17 16:57:12.011663', 0, 403);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (412, 'countyadmin2', 'localhost:8888', 'C654AB5830CB49B7A27A2DCE5C02D69A8FD289D95AE3580580407BBC9532AE24', '/county-dashboard', 200, '2024-06-17 16:57:17.041875', 0, 409);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (413, 'countyadmin2', 'localhost:8888', '00AF1E4619AEDD85A0E054169B8D682BA94E409350C2FC0278F8F19C2290BC39', '/contest/county', 200, '2024-06-17 16:57:17.053603', 0, 412);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (414, 'stateadmin1', 'localhost:8888', '1AA83EB279E341B2F960B9909ED1B87910C8FC295F71B6198C55FE73B5E023AC', '/dos-dashboard', 200, '2024-06-17 16:57:21.05217', 0, 413);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (415, 'stateadmin1', 'localhost:8888', '4C73F71D200244B5B6BE7C1A428998A8A99EBCEC6F3BFB700140C0598196E607', '/contest', 200, '2024-06-17 16:57:21.084348', 0, 414);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (416, 'countyadmin2', 'localhost:8888', '134F2287ABE3AAD7CC7C630131F2E6487F21189EE8DE51115CECEF58B6DAE4A5', '/audit-board-asm-state', 200, '2024-06-17 16:57:22.056407', 0, 415);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (422, 'countyadmin2', 'localhost:8888', '18D8D6FB4E55FB19719FCE784D5192182FDC9E3231A0984EED8BE9114BADAF7E', '/audit-board-asm-state', 200, '2024-06-17 16:57:27.079422', 0, 421);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (285, 'countyadmin2', 'localhost:8888', 'B299AAD3CEF2EB4ADBB39D33897D242A32C4D8EA687626F9DF22C728D637546B', '/audit-board-asm-state', 200, '2024-06-17 16:56:51.853276', 0, 284);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (290, 'countyadmin2', 'localhost:8888', 'B631AA679938542E594C0AD40ACECE63E9DFB6A51E40B2CC6EBC15C3B37A49AA', '/audit-board-asm-state', 200, '2024-06-17 16:56:56.876504', 0, 289);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (298, 'stateadmin1', 'localhost:8888', '2852C6D926245C823A237F8FE885D8BA669C78CDF3D532BFDB0B69E431A475AD', '/dos-dashboard', 200, '2024-06-17 16:57:00.338186', 0, 294);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (299, 'stateadmin1', 'localhost:8888', '2F0D1C151847610969D1753135CAA3EC806D4EFE3B493485CA8E689635452E69', '/dos-dashboard', 200, '2024-06-17 16:57:00.482656', 0, 298);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (300, 'stateadmin1', 'localhost:8888', '3F8E27C5A33831E1230914C352D0E6619E3519258AAC1B21989DCE486557CECD', '/contest', 200, '2024-06-17 16:57:00.512408', 0, 299);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (301, 'stateadmin1', 'localhost:8888', '9265F1352C6417B1F1D3B2A86A3C5F7EC5841E532A8987A699720AC5D9C15493', '/set-contest-names', 200, '2024-06-17 16:57:01.889025', 0, 300);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (302, 'countyadmin2', 'localhost:8888', '50AC8A51DD046B4716D6463BBF156B553560433E6EAACEEDDF7624AA9E11D91F', '/audit-board-asm-state', 200, '2024-06-17 16:57:01.897956', 0, 301);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (309, 'stateadmin1', 'localhost:8888', 'CF8D53C45D6750D2D35529EFD0CDC39FE1E327E39DA0C1FB456AC06439555492', '/contest', 200, '2024-06-17 16:57:04.95678', 0, 308);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (314, 'countyadmin2', 'localhost:8888', '94510F22A91DAB9EF3368C44A284D62086E2B8CD320535EC7A1AD09D49584E69', '/county-dashboard', 200, '2024-06-17 16:57:06.933045', 0, 311);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (315, 'countyadmin2', 'localhost:8888', '97D569C3578D3F942F3299C4C7932422AC034DB79BDEB8F7A68AA34121B3E1AB', '/contest/county', 200, '2024-06-17 16:57:06.946379', 0, 314);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (316, 'stateadmin1', 'localhost:8888', '1061FED2C0CE51B1C3169877EEEFF56D8ADD27207F341614614313D943E38406', '/random-seed', 200, '2024-06-17 16:57:09.224997', 0, 315);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (317, 'stateadmin1', 'localhost:8888', 'E4D63E9F669FDE1C8DECCDAFFCF952AEB1A29AB03FB6FABC0E53218FF2BC3EA3', '/contest', 200, '2024-06-17 16:57:09.253976', 0, 316);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (319, 'stateadmin1', 'localhost:8888', '512EDBD802634D0CE720B4F94BF2DBA09D7E19371A5B70C00A4CC344F72312CB', '/dos-dashboard', 200, '2024-06-17 16:57:09.390241', 0, 317);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (400, 'stateadmin1', 'localhost:8888', 'DAAB450CF41821BB2C55960A96420341F3FE345AB6EDBDDFC6DBDC84255A34C6', '/start-audit-round', 200, '2024-06-17 16:57:10.485419', 0, 319);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (401, 'stateadmin1', 'localhost:8888', '1CB31835B1FE17801406F6638327A6C289F1B151F265FA70A6CD4FD85AFE5EC7', 'illegal transition attempt on /start-audit-round: Illegal transition on ASM DoSDashboardASM/DoS: (DOS_INITIAL_STATE, DOS_START_ROUND_EVENT)', 403, '2024-06-17 16:57:10.488649', 0, 400);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (402, 'stateadmin1', 'localhost:8888', '991FA734326542AF80A37DA14F964A358DB08CD524D701823190CCD6E36E5740', '/dos-dashboard', 200, '2024-06-17 16:57:10.653844', 0, 401);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (403, 'stateadmin1', 'localhost:8888', 'D801674B8A87DFB6D45D527097E3D775F783D5DCE3AEF4E7D053041F1B6E5D33', '/contest', 200, '2024-06-17 16:57:10.682883', 0, 402);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (406, 'countyadmin2', 'localhost:8888', '068B981A1433C532F16DB38A7AA634E87B57CDDFACB25AC57C3404B1E815FFAF', '/county-dashboard', 200, '2024-06-17 16:57:12.014641', 0, 403);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (407, 'countyadmin2', 'localhost:8888', '1940D96571AE9CE12C0E2B2465C2995B0DD5D1D18702486D296D007E2297F2C3', '/contest/county', 200, '2024-06-17 16:57:12.034274', 0, 406);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (408, 'stateadmin1', 'localhost:8888', 'ABCAF920EA75007571C1E5E6C7CD32D87BDFB25B63D0ABB34BBD48A70A2036C6', '/dos-dashboard', 200, '2024-06-17 16:57:15.776857', 0, 407);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (409, 'stateadmin1', 'localhost:8888', '761EE324BD7CE93550DB6B50C9232022AEA0A9C5D40B3D01306875EA09ECF83E', '/contest', 200, '2024-06-17 16:57:15.81585', 0, 408);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (410, 'countyadmin2', 'localhost:8888', '56AD3E60AF6BEA6773677409D5E6E22EF9D8DCBBECE76F4666CAE77643F952CD', '/audit-board-asm-state', 200, '2024-06-17 16:57:17.03748', 0, 409);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (417, 'countyadmin2', 'localhost:8888', 'A1ACE41307C9E35144003B2058FB537E085B4D51D9EE4DB0A2042347728D147D', '/county-asm-state', 200, '2024-06-17 16:57:22.056457', 0, 415);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (423, 'countyadmin2', 'localhost:8888', '627B1D8690DCADFB058F8F581F717B7603D02DDBFBFFDA2ECBC8AC57C03FD5B7', '/county-asm-state', 200, '2024-06-17 16:57:27.079358', 0, 421);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (286, 'countyadmin2', 'localhost:8888', '608C9942A5155AC2CC8D090A04D92BC1B9C8FD49DB0108C351F5C53EF70E7178', '/county-asm-state', 200, '2024-06-17 16:56:51.853208', 0, 284);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (292, 'countyadmin2', 'localhost:8888', 'D11A1E65F648F01EB77B32C27FA68B5885FEB863ECF0C0B7324ADECC71CEB6E7', '/county-dashboard', 200, '2024-06-17 16:56:56.881507', 0, 289);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (293, 'countyadmin2', 'localhost:8888', '2DABB4B7D10800DD9DE8A3A157D96DA46D0B881A25959365710DC6E36D8EB2D5', '/contest/county', 200, '2024-06-17 16:56:56.898644', 0, 292);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (294, 'stateadmin1', 'localhost:8888', 'CD8EDB7428DE7ECDBFDE3B553B138769C9315B0364A00B67B89D4BFF3696C5DD', '/update-audit-info', 200, '2024-06-17 16:57:00.167581', 0, 293);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (295, 'stateadmin1', 'localhost:8888', 'B1EA4915D1D3CFD11F4F98F3564C79C0069921FDA69A1C08BD6CED9791F09BEB', '/contest', 200, '2024-06-17 16:57:00.197598', 0, 294);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (304, 'countyadmin2', 'localhost:8888', '31940E2E7DBE39E0C6D78C11E20EFCAD1CAB46C8758DFB70DF1BDB008A82BA59', '/county-dashboard', 200, '2024-06-17 16:57:01.902796', 0, 301);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (306, 'stateadmin1', 'localhost:8888', 'E47B2380DCBDE4F8C69620387728D517A81D35A6D2F4D9B46CF60F50ADFD893F', '/dos-dashboard', 200, '2024-06-17 16:57:02.010141', 0, 304);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (307, 'stateadmin1', 'localhost:8888', '6B05DCD7EB98E15425DB17837A63CD365929A61C9784B89308488577481C55C4', '/contest', 200, '2024-06-17 16:57:02.105081', 0, 306);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (308, 'stateadmin1', 'localhost:8888', '725198F1935AFD08732290211042A8B8C4DF76720ABF7ECB44F2986CAF202CF2', '/select-contests', 200, '2024-06-17 16:57:04.903745', 0, 307);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (311, 'stateadmin1', 'localhost:8888', '84BE6EBBDAB24D725236A5CF4BB182C297DAD45668339991D4DAFE21816DA87C', '/dos-dashboard', 200, '2024-06-17 16:57:05.060136', 0, 308);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (312, 'countyadmin2', 'localhost:8888', '0A6A659146F5108B72EC3F1D73A2C0885595B039CC1348CEC720172187EEC61F', '/audit-board-asm-state', 200, '2024-06-17 16:57:06.927272', 0, 311);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (404, 'countyadmin2', 'localhost:8888', '20342C769EE64B208D9329D3E3E13825843381C1B69FAE14A63D63F59C3DCD81', '/county-asm-state', 200, '2024-06-17 16:57:12.011654', 0, 403);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (411, 'countyadmin2', 'localhost:8888', 'FEEA46931BC1A15C2A4AE15316249231C3BD8E97D300240796076CB322F3418C', '/county-asm-state', 200, '2024-06-17 16:57:17.037952', 0, 409);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (418, 'countyadmin2', 'localhost:8888', '64DEB3BD5CB07716FA3C55FA262BCED063CC535FC4F292C3C7929F3A613B2EAE', '/county-dashboard', 200, '2024-06-17 16:57:22.060353', 0, 415);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (419, 'countyadmin2', 'localhost:8888', '7E26CBD2F411FB1EF05C6F7861F0C8C0D80660A50A43C48AD9FD15252D602135', '/contest/county', 200, '2024-06-17 16:57:22.076055', 0, 418);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (420, 'stateadmin1', 'localhost:8888', 'C135BED5C19D14288687AB9244CF6B8A2DC83764617432E5F01A6C04D6E783B5', '/dos-dashboard', 200, '2024-06-17 16:57:26.200228', 0, 419);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (421, 'stateadmin1', 'localhost:8888', '7D0D4D2F5B280D14AEE82D1D53324482353693C3505C52E6534573F7A92D78BB', '/contest', 200, '2024-06-17 16:57:26.229541', 0, 420);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (424, 'countyadmin2', 'localhost:8888', '01C64FB54662B024463A7518AB6E078D50CAF9F2CCB05176740DDE82BEA792E0', '/county-dashboard', 200, '2024-06-17 16:57:27.083425', 0, 421);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (425, 'countyadmin2', 'localhost:8888', 'A6F425BAC13F0C3C698581A26E2AB5CD9D28F3CF73867F6D3DB46D0862EEE458', '/contest/county', 200, '2024-06-17 16:57:27.099832', 0, 424);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (426, 'stateadmin1', 'localhost:8888', '57350CFF400249FCEDF596B72431AD615CF3F211826C290D9547C1CD4A5DFB10', '/dos-dashboard', 200, '2024-06-17 16:57:31.349831', 0, 425);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (427, 'stateadmin1', 'localhost:8888', '5F69D18E9580CF2D184FE203D4FC5B34C3D68B9575D6E6B5E27A20034F641E75', '/contest', 200, '2024-06-17 16:57:31.381326', 0, 426);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (428, 'countyadmin2', 'localhost:8888', '89BE75BB56BB36ACB962B424AFD9265FAC2B23A335393F381505CB77B4ABFA09', '/county-asm-state', 200, '2024-06-17 16:57:32.10116', 0, 427);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (429, 'countyadmin2', 'localhost:8888', 'CF3A44CCF895667568DFBA56667E1F1B05DFA32FCB490AE0D116277F62ED2172', '/audit-board-asm-state', 200, '2024-06-17 16:57:32.10143', 0, 427);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (430, 'countyadmin2', 'localhost:8888', '34FAAB7DD06333A3F9FC3AFD6AC39E1ABC512D8F631F8C28C54E0EE0BF5DED6C', '/county-dashboard', 200, '2024-06-17 16:57:32.106209', 0, 427);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (431, 'countyadmin2', 'localhost:8888', '4B0BA0350EB412C2AE2961E9E618DD89ABA0896ABA5D1012863E49284D27C16F', '/contest/county', 200, '2024-06-17 16:57:32.121526', 0, 430);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (432, 'stateadmin1', 'localhost:8888', 'A7D92C4197EED2A3AFE1EB14C8CCFA5CAF442EC66DE1DEAF7109E956F5F5A6F1', '/dos-dashboard', 200, '2024-06-17 16:57:36.533348', 0, 431);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (433, 'stateadmin1', 'localhost:8888', 'EF3EF51526DD238629E44C401A8484AF775CB0B84EF5A485937CFC4A6848E5E4', '/contest', 200, '2024-06-17 16:57:36.561537', 0, 432);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (434, 'countyadmin2', 'localhost:8888', 'CB09D5636973D81FEAA608C5D5695AC61D7090D862CBAE061AD762ACA4D2B0EA', '/audit-board-asm-state', 200, '2024-06-17 16:57:37.127533', 0, 433);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (435, 'countyadmin2', 'localhost:8888', '8CDD52AA821A0789357FE0FFBB2904050E6D934CEB26A2A35D28794D5527F837', '/county-asm-state', 200, '2024-06-17 16:57:37.127643', 0, 433);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (436, 'countyadmin2', 'localhost:8888', '5539BF11A4C442CC75409BC60ECDA2707BD2D0D1AC9571D4F86A0C070D436C6B', '/county-dashboard', 200, '2024-06-17 16:57:37.131443', 0, 433);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (437, 'countyadmin2', 'localhost:8888', '7FCC80AFCB357783C713AC6001F96A74FEF5F24B300BD11180D91FB62AEB3DE7', '/contest/county', 200, '2024-06-17 16:57:37.165508', 0, 436);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (438, 'stateadmin1', 'localhost:8888', '67A8123F6F88A636E7B0451646F674BB9A605056920BA369BA1757F45E274F0E', '/dos-dashboard', 200, '2024-06-17 16:57:41.709182', 0, 437);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (439, 'stateadmin1', 'localhost:8888', '76BFFDACF2C2E42BB93169608904054D932A7AB717999CC4BEDC44E6B671280B', '/contest', 200, '2024-06-17 16:57:41.735663', 0, 438);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (441, 'countyadmin2', 'localhost:8888', '1D9FECFB22CBF457D58B452F9D9D6D220DCE69CC7996F99D305FC0FB0EBC92E7', '/county-asm-state', 200, '2024-06-17 16:57:42.170859', 0, 439);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (440, 'countyadmin2', 'localhost:8888', 'FB4489FAE1E7EDC41CAD1B499FAE6E9B7D125741DCF2B8EE14569638A59F1E58', '/audit-board-asm-state', 200, '2024-06-17 16:57:42.170848', 0, 439);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (442, 'countyadmin2', 'localhost:8888', 'FB145EE5CEB50968F909403F5C3059436BDE7D5788407376A3D595140AAC8770', '/county-dashboard', 200, '2024-06-17 16:57:42.175242', 0, 439);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (443, 'countyadmin2', 'localhost:8888', '20E8148B0148177356EDAB97C0951FC233F399331542DF93CE55D7ABD3D36FDC', '/contest/county', 200, '2024-06-17 16:57:42.208099', 0, 442);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (444, 'stateadmin1', 'localhost:8888', 'EAFC423FB821CFD4C81FF83B63CFD10A067A6CECCA75E52C24FB4B46481A33B6', '/dos-dashboard', 200, '2024-06-17 16:57:46.826419', 0, 443);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (445, 'stateadmin1', 'localhost:8888', 'D7A8FECD7AF2134AF77BAE27B3F00B11682F79F08C9F9E314BB52339B62CFCD5', '/contest', 200, '2024-06-17 16:57:46.852313', 0, 444);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (446, 'countyadmin2', 'localhost:8888', 'A2BC112054F02641C6A8CD31759B07F348F6E4C3E7B9976148A3E81F56FB513B', '/county-asm-state', 200, '2024-06-17 16:57:47.210016', 0, 445);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (447, 'countyadmin2', 'localhost:8888', '6E93BAC2DEE137EC84C1260E2CCA3CAB32F2D805EFD5A0E1C498995373AA70B3', '/audit-board-asm-state', 200, '2024-06-17 16:57:47.210033', 0, 445);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (448, 'countyadmin2', 'localhost:8888', '8078ABA82F5CB512559649B7CE1286813AC5FC649576DDA48DC42D7715A0B3E1', '/county-dashboard', 200, '2024-06-17 16:57:47.213877', 0, 445);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (449, 'countyadmin2', 'localhost:8888', 'BA43EF0AC63AEB09CE2EC9C5198BB838D70315CE8FAA75B6147F2C61E19C9BE7', '/contest/county', 200, '2024-06-17 16:57:47.232202', 0, 448);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (450, 'stateadmin1', 'localhost:8888', 'BA0F3EB000EC9A6BC215E2FEEFCEEAD96D7011BFDE018C7B7ECECE242B8D7F44', '/dos-dashboard', 200, '2024-06-17 16:57:51.971423', 0, 449);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (451, 'stateadmin1', 'localhost:8888', '9054D90F09911F0B5E8387244C033F854A72D733A28AFE8E1F7C91E32973F5DB', '/contest', 200, '2024-06-17 16:57:51.999324', 0, 450);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (452, 'countyadmin2', 'localhost:8888', 'FA9429BB866F5EB96C5B60F7FFF7F796C5E0D64A985A48796BADF7B7CF158684', '/audit-board-asm-state', 200, '2024-06-17 16:57:52.233742', 0, 451);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (453, 'countyadmin2', 'localhost:8888', '9570BE760847524A44985FB84BBB731A4178DE204136668C587724DF7BBF597B', '/county-asm-state', 200, '2024-06-17 16:57:52.234133', 0, 451);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (458, 'countyadmin2', 'localhost:8888', '8D0178C6DDBB2AA4C9901BE91C31391A1B92A5399D2E3C732BB8260BDA4BC93D', '/audit-board-asm-state', 200, '2024-06-17 16:57:57.255387', 0, 457);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (465, 'countyadmin2', 'localhost:8888', '6FA6B76FB771DC4C763653DB81B7648BCC6CACDA8EC262F150FE713517136A96', '/audit-board-asm-state', 200, '2024-06-17 16:58:02.276139', 0, 463);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (468, 'countyadmin2', 'localhost:8888', '74832D08FD800A9FFCE0901B44FB4E431DDC62697022D405E1C5217EF87E5552', '/audit-board-asm-state', 200, '2024-06-17 16:58:07.44024', 0, 467);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (454, 'countyadmin2', 'localhost:8888', 'BFB3554FA8240986D2D8A8A26BA975A5865672B47BB963AE304FF7AEE261B199', '/county-dashboard', 200, '2024-06-17 16:57:52.237747', 0, 451);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (455, 'countyadmin2', 'localhost:8888', '15DBBAC26F5CEE2C3568451563B9E464E56CB535DFAFEC3332D70CB8592FF855', '/contest/county', 200, '2024-06-17 16:57:52.254724', 0, 454);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (456, 'stateadmin1', 'localhost:8888', '1605E4D1F8CA08B3AB1F10C20AE4D5D8F5E94925F1BA9B0F5C777E9ADFC34BA2', '/dos-dashboard', 200, '2024-06-17 16:57:57.091964', 0, 455);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (457, 'stateadmin1', 'localhost:8888', 'FA60813FDE6547624CA5AC46E7B0502E4D7FFF3624BBFB3064076B0F1539A3F7', '/contest', 200, '2024-06-17 16:57:57.118755', 0, 456);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (460, 'countyadmin2', 'localhost:8888', '6DFA2FF2E47B921D62DC703FBD7F0E8655AD2E94903794B6891979FA0BE37FBD', '/county-dashboard', 200, '2024-06-17 16:57:57.25982', 0, 457);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (461, 'countyadmin2', 'localhost:8888', '27E67076AF3C8E24F5CB60D8807555D87E3F2B4F6C58AEC0D64B5F9B0B676B55', '/contest/county', 200, '2024-06-17 16:57:57.274502', 0, 460);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (462, 'stateadmin1', 'localhost:8888', 'B7F3F3A173E4F0BD41495E6E0D0BA1BF7AA3856D5D5B0BDBCF4FFD992B8B4ADD', '/dos-dashboard', 200, '2024-06-17 16:58:02.201062', 0, 461);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (463, 'stateadmin1', 'localhost:8888', '150B7FB487169342AA42ABD083B4385C002E230596C63527B4BAA19347347DC3', '/contest', 200, '2024-06-17 16:58:02.230317', 0, 462);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (466, 'countyadmin2', 'localhost:8888', '01FF772064D126B7CD34593CFDD23E21F025D920F9CFD1840DBD9E80C838E33D', '/county-dashboard', 200, '2024-06-17 16:58:02.27962', 0, 463);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (467, 'countyadmin2', 'localhost:8888', '2CBC6718880B79C1639FBBBCB55CB0A8E6E325F64629B007DB0CDBAB83B2C515', '/contest/county', 200, '2024-06-17 16:58:02.293133', 0, 466);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (472, 'stateadmin1', 'localhost:8888', '85A9464826D5D04A4D3533EF2C82BB520F94D1296CD14BA00335EF58CB7B9488', '/dos-dashboard', 200, '2024-06-17 16:58:07.530337', 0, 467);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (473, 'stateadmin1', 'localhost:8888', 'C8F7B136D8D954D01777E2723091BD2216BEEE0D525E6B060D3D5205DE36127F', '/contest', 200, '2024-06-17 16:58:07.571074', 0, 472);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (475, 'countyadmin2', 'localhost:8888', '7A38D25B287DA19CD3D406387DA6CE5DD0AF3A38B35BB43DD83FB358EB33775B', '/audit-board-asm-state', 200, '2024-06-17 16:58:12.461916', 0, 473);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (481, 'countyadmin2', 'localhost:8888', 'B4DAD3AE66F325108B4E4180E954E86C378D94564B4E77103A41D451DEA6CF06', '/county-asm-state', 200, '2024-06-17 16:58:17.48632', 0, 479);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (487, 'countyadmin2', 'localhost:8888', '9867B60FBC23B2AA81F9F6A9CD3B869A99AFC70A9A121DB72B8A8EA3302CB08E', '/county-asm-state', 200, '2024-06-17 16:58:22.509889', 0, 485);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (494, 'countyadmin2', 'localhost:8888', '5355A021122BC38704FE8081817A6685EF38688712026037A799EE4B455CB8E1', '/county-dashboard', 200, '2024-06-17 16:58:27.532177', 0, 491);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (495, 'countyadmin2', 'localhost:8888', '14E30BD08F15C88B94A8E90E2966E554544465E8CE5E45C6F87FE1D7E5DA4FC9', '/contest/county', 200, '2024-06-17 16:58:27.547201', 0, 494);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (496, 'stateadmin1', 'localhost:8888', 'E94FA34972D2EBBFBBD45EE7AE4105F218C23D73D39D96546D65C722A4E78852', '/dos-dashboard', 200, '2024-06-17 16:58:28.159482', 0, 495);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (497, 'stateadmin1', 'localhost:8888', '44BF188506212DCC2C1F313A662A2735EBC4DC847482B9DB42D059A2CBA5DE4D', '/contest', 200, '2024-06-17 16:58:28.19069', 0, 496);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (459, 'countyadmin2', 'localhost:8888', 'F9D6424D3E577020FDA4C38EE4C1286253A3FDC6EA269DBA9BD8C445DC1BE751', '/county-asm-state', 200, '2024-06-17 16:57:57.255387', 0, 457);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (464, 'countyadmin2', 'localhost:8888', '466934C969AD650C93E70094E3CE5162D8741B177A51FD7AE0E431DDE4B90F7B', '/county-asm-state', 200, '2024-06-17 16:58:02.275906', 0, 463);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (469, 'countyadmin2', 'localhost:8888', '34F641D8DEE816D61700BBD2B48625FB5FB572FBDE1F7160F017FA980433E40C', '/county-asm-state', 200, '2024-06-17 16:58:07.440591', 0, 467);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (470, 'countyadmin2', 'localhost:8888', '25A60B24D8ED4B8BD21D414136954EF1B0DE596C7C034F65EEA5550046F8984E', '/county-dashboard', 200, '2024-06-17 16:58:07.444011', 0, 467);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (471, 'countyadmin2', 'localhost:8888', '40C7662E4B213CB6D824D5AB8DAE2021DF7F8AD7C9744592DBFA96ECEE93A7F2', '/contest/county', 200, '2024-06-17 16:58:07.459333', 0, 470);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (474, 'countyadmin2', 'localhost:8888', '188B9B2C21E778C88AD1932F3BC43D41B797A39CAA1F77B350CF947D91E238FA', '/county-asm-state', 200, '2024-06-17 16:58:12.461836', 0, 473);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (476, 'countyadmin2', 'localhost:8888', '3AA5ACFF176120F57777457BE36882CD13CD1DBA706B8DCBD2FBE58B753AE7AC', '/county-dashboard', 200, '2024-06-17 16:58:12.464752', 0, 473);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (477, 'countyadmin2', 'localhost:8888', '599FD4D89353847E6D4DE36DABA1EFE86FA1799ED7EF172FC24297B5A094C085', '/contest/county', 200, '2024-06-17 16:58:12.481224', 0, 476);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (478, 'stateadmin1', 'localhost:8888', 'AF93B219016EE3DF181C862478AC717C8CEDE80B9D4FE7B92C7496C0C563C3DC', '/dos-dashboard', 200, '2024-06-17 16:58:12.696522', 0, 477);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (479, 'stateadmin1', 'localhost:8888', '474BC4D7DF32D20DCD370F57A8259D0A312BA9628201CB7B9C812A3E8753B8B0', '/contest', 200, '2024-06-17 16:58:12.726493', 0, 478);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (480, 'countyadmin2', 'localhost:8888', '26224386D6B277E409FA93DCB88794A5890E898D112DCBBD42D5852EEA412F5B', '/audit-board-asm-state', 200, '2024-06-17 16:58:17.486318', 0, 479);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (482, 'countyadmin2', 'localhost:8888', '7E0706DFA470FE9601ADD6BD9B4D1DA3DDE475669D6E9A1FE19D02E955C1569A', '/county-dashboard', 200, '2024-06-17 16:58:17.48919', 0, 479);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (483, 'countyadmin2', 'localhost:8888', 'E949E5B186BF821B37453C8C10A15EBE8AFDCF61489F39378A3D246D5E499122', '/contest/county', 200, '2024-06-17 16:58:17.507775', 0, 482);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (484, 'stateadmin1', 'localhost:8888', '3CD7E5E8EAED695994B633B05DD53B349921CA4E0EE7B792C25B007D547DA429', '/dos-dashboard', 200, '2024-06-17 16:58:17.851681', 0, 483);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (485, 'stateadmin1', 'localhost:8888', '8FC28D00858F7ADBCFFAA2078A520A4D7FBCB8BC255319F2F817A85C63DADE48', '/contest', 200, '2024-06-17 16:58:17.880429', 0, 484);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (486, 'countyadmin2', 'localhost:8888', '4C1BF6059C6BB13A0943CDEF4E058D57E4F961AD20283EE815C68D35F306653E', '/audit-board-asm-state', 200, '2024-06-17 16:58:22.509825', 0, 485);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (488, 'countyadmin2', 'localhost:8888', 'D216672F08AA5A4F9E3107FB30FF3ECD0D533038361C569DEB586DF9CDB1E760', '/county-dashboard', 200, '2024-06-17 16:58:22.512944', 0, 485);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (489, 'countyadmin2', 'localhost:8888', 'C4C479E7D64F5C874F8A4CB538A18D670AB641E89563510ABA12A6D9558FB5F6', '/contest/county', 200, '2024-06-17 16:58:22.526512', 0, 488);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (490, 'stateadmin1', 'localhost:8888', '0D408624178B75B3D92E30A834ABB124F03855EDB2865D6B8C1CF5592C3EFA92', '/dos-dashboard', 200, '2024-06-17 16:58:23.022127', 0, 489);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (491, 'stateadmin1', 'localhost:8888', '11365FC92E909C8F7B8EC0863B0278407053BC2117AAA858B39D1A8610D7E08F', '/contest', 200, '2024-06-17 16:58:23.050932', 0, 490);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (493, 'countyadmin2', 'localhost:8888', '897B4BF46D4D52859760878879E885111425180B102D2184711BF368F5AEFF0E', '/county-asm-state', 200, '2024-06-17 16:58:27.528435', 0, 491);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (492, 'countyadmin2', 'localhost:8888', '99FBDE14E68600885BFDE922DAE9C44B1BC0BAAAD3B4D4528337D01252718C02', '/audit-board-asm-state', 200, '2024-06-17 16:58:27.52848', 0, 491);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (498, 'countyadmin2', 'localhost:8888', '36E8801D15A7397FB4117D0F8D22C9B5320C96A2BEBD15FB735E8C3975C8719C', '/audit-board-asm-state', 200, '2024-06-17 16:58:32.548978', 0, 497);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (499, 'countyadmin2', 'localhost:8888', 'D19639A0EEFC3600021C58D74B3CC16B4B82606CE5EFC30598FF28FB177984EB', '/county-asm-state', 200, '2024-06-17 16:58:32.549089', 0, 497);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (500, 'countyadmin2', 'localhost:8888', '9FC37EE5E1F8FAB5BEC5B089A1FE4BDB92BA7525344AF767CAE14C6516A5ADB5', '/county-dashboard', 200, '2024-06-17 16:58:32.553397', 0, 497);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (501, 'countyadmin2', 'localhost:8888', 'D57FC7787F2764B1A6B01D7B3F7632D9C4611A67FE2E6BAC801705D16A131904', '/contest/county', 200, '2024-06-17 16:58:32.568365', 0, 500);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (502, 'stateadmin1', 'localhost:8888', '45538D2517ABB053916761DD4C7BE778FEFF5AB2A1F0B6D5072D49795D5F7BBA', '/dos-dashboard', 200, '2024-06-17 16:58:33.312841', 0, 501);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (503, 'stateadmin1', 'localhost:8888', 'E53067A4AC9734AF0C7556E72615321639A039EB354B8E36A1FD0D455DDCC55D', '/contest', 200, '2024-06-17 16:58:33.344017', 0, 502);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (504, 'countyadmin2', 'localhost:8888', 'C6A2B86F73966C5A5916DB6AC4492FADE13E99954A6DE0FF24CD783758F417B3', '/county-asm-state', 200, '2024-06-17 16:58:37.577347', 0, 503);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (505, 'countyadmin2', 'localhost:8888', '7BD8AAF5FE5A4263D5389B39E3145CECB6B7A8D9B85C1D24DEACCB0A59A06483', '/audit-board-asm-state', 200, '2024-06-17 16:58:37.577552', 0, 503);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (506, 'countyadmin2', 'localhost:8888', '60E3A25FAC2D167A65A32CC7D79F4B3B6CC681C2FC72F7C7B09A772E78D4E643', '/county-dashboard', 200, '2024-06-17 16:58:37.581517', 0, 503);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (507, 'countyadmin2', 'localhost:8888', 'C9CE51B59F3F16E5C2010BEEF7C3D55BB56F731B1EF3552B76D3A232D08B076A', '/contest/county', 200, '2024-06-17 16:58:37.619123', 0, 506);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (508, 'stateadmin1', 'localhost:8888', '4F3FF77818D2D7EDC796A5EF1E84B6EB829B8D03C04796E1FD09776EE8574B6B', '/dos-dashboard', 200, '2024-06-17 16:58:38.442837', 0, 507);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (509, 'stateadmin1', 'localhost:8888', '80287A642F8FBC4FB5B562065856D52597829668716795BFD68000B0611F02C7', '/contest', 200, '2024-06-17 16:58:38.473525', 0, 508);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (510, 'countyadmin2', 'localhost:8888', '9AB94B33A2ED293D365E42B01C26DB2736467F773D31E9E795E898911635BCA8', '/county-asm-state', 200, '2024-06-17 16:58:42.621873', 0, 509);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (511, 'countyadmin2', 'localhost:8888', '4AA1150FD9012F73373AE929A0A39454E81E730BD316AF7D143C92B33B38C332', '/audit-board-asm-state', 200, '2024-06-17 16:58:42.621859', 0, 509);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (512, 'countyadmin2', 'localhost:8888', '06E1B8D2D3547753866D2DAEB44C5366E79B2E4DC86A85BD0A4B1CD47083D4B9', '/county-dashboard', 200, '2024-06-17 16:58:42.624881', 0, 509);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (513, 'countyadmin2', 'localhost:8888', '86A6CF54A1E8F2F53E9FE40601829421885025778C0BA7BEE33469A182A04A91', '/contest/county', 200, '2024-06-17 16:58:42.644689', 0, 512);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (514, 'stateadmin1', 'localhost:8888', '05D1080687A17C6768A91783568258E3796A68AE8715D9CAD1574643E5A801FD', '/dos-dashboard', 200, '2024-06-17 16:58:43.590659', 0, 513);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (515, 'stateadmin1', 'localhost:8888', 'BED98D94CF4B8AD7BB52F9D0A13ECEC692A27D2C169DB2D0768682DC8EB291A5', '/contest', 200, '2024-06-17 16:58:43.619944', 0, 514);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (516, 'countyadmin2', 'localhost:8888', 'A786BC4602837D8A022F72987E0EE703A23CAA4CE43029D3F6C3DFE27BEECE7D', '/audit-board-asm-state', 200, '2024-06-17 16:58:47.64782', 0, 515);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (517, 'countyadmin2', 'localhost:8888', '38EB82E8E7E530981D3CB547AF866FB9B15D7DC07B52D864A917F9BF105EDA7C', '/county-asm-state', 200, '2024-06-17 16:58:47.647905', 0, 515);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (518, 'countyadmin2', 'localhost:8888', '17DF4CDFDAC9CBCB28FA21950E6D9B94EFA2F81DD83C08C0BF6C8DC477759AB8', '/county-dashboard', 200, '2024-06-17 16:58:47.651827', 0, 515);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (519, 'countyadmin2', 'localhost:8888', 'BB0BE334D4A7C62613D360DAFE2AF440AF2B456D1AFFD5F4806499698A524B1D', '/contest/county', 200, '2024-06-17 16:58:47.66818', 0, 518);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (520, 'stateadmin1', 'localhost:8888', '81D82E94CB86F24A1A08871B296022E63339007CC16FAB605424209FD5848361', '/dos-dashboard', 200, '2024-06-17 16:58:48.724934', 0, 519);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (521, 'stateadmin1', 'localhost:8888', '75D60036C3AD1B92BA0EDC0BAF23CE380C5677AAAF4E0BEE4B3060EC033BE2D3', '/contest', 200, '2024-06-17 16:58:48.753309', 0, 520);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (523, 'countyadmin2', 'localhost:8888', '6E16F7B25FCBE9FFDA3298B206113A76D32335C6E984E9C2EC6CEE299CEF6026', '/county-asm-state', 200, '2024-06-17 16:58:52.670437', 0, 521);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (529, 'countyadmin2', 'localhost:8888', '250C40B892DE5A42E1E2832127FF51E5E4667C4287B3E4070D4D501DB12C4A87', '/county-asm-state', 200, '2024-06-17 16:58:57.691139', 0, 527);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (535, 'countyadmin2', 'localhost:8888', 'E3B3779471DE743D8BBFD54160EB2B4D4AF2E325DFF77E5871DBB903F590FDAF', '/county-asm-state', 200, '2024-06-17 16:59:02.712647', 0, 533);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (541, 'countyadmin2', 'localhost:8888', '4CCF5CED5C93C84EFDCE5E134BBF9F0D3111B6A9E4A9D4CAA4E9B5EAAB5B0A3F', '/county-asm-state', 200, '2024-06-17 16:59:07.735438', 0, 539);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (546, 'countyadmin2', 'localhost:8888', '1D0CF36E0B6E33FC9CC9DB42EE7CF202F945DDAC3AB5971AE4787E61DAC4AFC7', '/audit-board-asm-state', 200, '2024-06-17 16:59:12.760622', 0, 545);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (554, 'countyadmin2', 'localhost:8888', 'BA0B61E7FE9B3F5C1F6DCCA95D553ECE238E49E1EA87BE3AA47119B5E5A10287', '/county-dashboard', 200, '2024-06-17 16:59:17.787132', 0, 551);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (555, 'countyadmin2', 'localhost:8888', 'D6EAA40B42DA1D698E7FC0E6956BAC68B2D325F15091F0609BC814AA76289BEF', '/contest/county', 200, '2024-06-17 16:59:17.805359', 0, 554);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (556, 'stateadmin1', 'localhost:8888', '8033F084593B48F6FB558B644DF3AD0CB07BC3939DAE458A6F3F297B7820735E', '/dos-dashboard', 200, '2024-06-17 16:59:19.657024', 0, 555);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (557, 'stateadmin1', 'localhost:8888', 'BCC5B0F94548E5B002A2015600764C094A06E6B5A187C5E2DEDE1CD30A5DEEF5', '/contest', 200, '2024-06-17 16:59:19.683989', 0, 556);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (560, 'countyadmin2', 'localhost:8888', 'BB6765EDBBDCF22615D518A4D6D7E5458184FEC4055CEA70098287DA9F066B8E', '/county-dashboard', 200, '2024-06-17 16:59:22.812158', 0, 557);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (561, 'countyadmin2', 'localhost:8888', 'F70B4E7E3A998506162EA2EE39942C111D3EA18ADC515C23DCB3B9FB92E982CB', '/contest/county', 200, '2024-06-17 16:59:22.824662', 0, 560);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (562, 'stateadmin1', 'localhost:8888', '19FB491071CAF056DDD57E26C27D0F74EE685C4F4185738836E892A1730462A9', '/dos-dashboard', 200, '2024-06-17 16:59:24.804127', 0, 561);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (563, 'stateadmin1', 'localhost:8888', 'B5826423B9C98CF02B77E625738CD7F71C88B0AC094CD15F86961064B24B0D41', '/contest', 200, '2024-06-17 16:59:24.833229', 0, 562);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (566, 'countyadmin2', 'localhost:8888', 'EF00C6EA65DAE9C271B91EF55E49AB30F0F055A54AEEAA6F8B006321F37A1F01', '/county-dashboard', 200, '2024-06-17 16:59:27.830753', 0, 563);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (567, 'countyadmin2', 'localhost:8888', '57B24C35B4B0EF2DA8C1701554015AE36EFF793FDFF6A535DDFF227C25B82868', '/contest/county', 200, '2024-06-17 16:59:27.845313', 0, 566);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (568, 'stateadmin1', 'localhost:8888', '8E483085658D3B944FCB53E3514FD8D49402B171513A0D01E0B2F2FCB221B694', '/dos-dashboard', 200, '2024-06-17 16:59:29.954264', 0, 567);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (569, 'stateadmin1', 'localhost:8888', 'BF0B68B235A5AA0C5C6A2975C041E49CAF96AF3F65547AD68F5672441F546982', '/contest', 200, '2024-06-17 16:59:29.982932', 0, 568);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (572, 'countyadmin2', 'localhost:8888', '02138F07D3BE90A13284B21EDDD54D8E0D74FC0818D872A28463CF22E95C961B', '/county-dashboard', 200, '2024-06-17 16:59:32.851432', 0, 569);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (573, 'countyadmin2', 'localhost:8888', '62B0A7A38A368C439A73799C4B67DD38E1A8AA57444419FD50D88F66C0754E30', '/contest/county', 200, '2024-06-17 16:59:32.867388', 0, 572);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (574, 'stateadmin1', 'localhost:8888', '783366B0E841E0D38814FE5E6CFE9CFE7302151125F7F58922200F933F085D03', '/dos-dashboard', 200, '2024-06-17 16:59:35.116036', 0, 573);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (575, 'stateadmin1', 'localhost:8888', '66C99C44BD5BE93A3E99C0123F6F181483F9514D7844948C2F461B45A64EE295', '/contest', 200, '2024-06-17 16:59:35.144773', 0, 574);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (578, 'countyadmin2', 'localhost:8888', 'CC462F54705D685E346BF0DCA688965827E4447F6F301F717062720CEF487A0C', '/county-dashboard', 200, '2024-06-17 16:59:37.872377', 0, 575);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (579, 'countyadmin2', 'localhost:8888', '8E8526A3B6CB6C546A3B5083F86BC408A9B689836ACE9ED3159C9C763B6A1760', '/contest/county', 200, '2024-06-17 16:59:37.889184', 0, 578);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (580, 'stateadmin1', 'localhost:8888', '742F813A65AC391A2349C8EF6C0751AB6AB0CED2446C0B95EF6392B7B4460D9F', '/dos-dashboard', 200, '2024-06-17 16:59:40.25877', 0, 579);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (581, 'stateadmin1', 'localhost:8888', 'BC8886D1656C4F4634E357D7CE0242222443E712E540E9D35C472ADC8D8E9DEF', '/contest', 200, '2024-06-17 16:59:40.286838', 0, 580);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (584, 'countyadmin2', 'localhost:8888', 'AF5B4739D07B20490577F434AC21508E9F7D52CED336CA0E2935D8ED01579B23', '/county-dashboard', 200, '2024-06-17 16:59:42.895583', 0, 581);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (585, 'countyadmin2', 'localhost:8888', 'FA5EB95DF74A314F977E41749169B403DCF72A980B378355FAB6D3AFE4D448DE', '/contest/county', 200, '2024-06-17 16:59:42.911024', 0, 584);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (586, 'stateadmin1', 'localhost:8888', '8135F3667ADE3AA06B449D30C70758246EFB7AD6ED74367E6F88F8CBEEFF4144', '/dos-dashboard', 200, '2024-06-17 16:59:45.398847', 0, 585);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (587, 'stateadmin1', 'localhost:8888', '200F0742A039EEE3BEDC089BCEC4A0D6BAE681A6A8BD8972C64738C3BDFF5327', '/contest', 200, '2024-06-17 16:59:45.42635', 0, 586);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (588, 'countyadmin2', 'localhost:8888', '7975F7A5348C46CFD7C8BD89762C8D583BBF530F54B2CCA61D6597EABB36427E', '/county-asm-state', 200, '2024-06-17 16:59:47.913178', 0, 587);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (596, 'countyadmin2', 'localhost:8888', 'E1E8613EDFC93637B1D44E7A88EE536F903CB13DF02B0FFCF57FFCE98596CDB0', '/county-dashboard', 200, '2024-06-17 16:59:52.940558', 0, 593);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (597, 'countyadmin2', 'localhost:8888', '2327740BCF51AF67BD00C0DA78C40C1DD583C01D44C85CB5AA559215FA79774F', '/contest/county', 200, '2024-06-17 16:59:52.955982', 0, 596);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (598, 'stateadmin1', 'localhost:8888', 'D849C002390FFCFCF49EB9F760309CDD80068D0D585EE0B47EAACC243FC6AFF5', '/dos-dashboard', 200, '2024-06-17 16:59:55.691327', 0, 597);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (599, 'stateadmin1', 'localhost:8888', '816CE369F55376A3A4C829A2352A20CB428FC7975AD9EDC85C7AA037A9CAC8D1', '/contest', 200, '2024-06-17 16:59:55.75074', 0, 598);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (602, 'countyadmin2', 'localhost:8888', 'A7A3D12169E7C6377BABF2159B4B24C374E97337484A509F9101FC91762D428C', '/county-dashboard', 200, '2024-06-17 16:59:57.961549', 0, 599);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (603, 'countyadmin2', 'localhost:8888', '0873EE05E27A3C1FFBDB59FBBA8EBC2B576768D397632825393DC20A9829609A', '/contest/county', 200, '2024-06-17 16:59:57.974915', 0, 602);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (604, 'stateadmin1', 'localhost:8888', '08812EF04A5B3A2E8D9E6282D63535A2AB2CAFF2F22691402D54921E8AB1B184', '/dos-dashboard', 200, '2024-06-17 17:00:00.864782', 0, 603);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (605, 'stateadmin1', 'localhost:8888', '7676B520A3D3D8F629E0686510770306971F5EA206045276DC4089F167D45186', '/contest', 200, '2024-06-17 17:00:00.893501', 0, 604);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (608, 'countyadmin2', 'localhost:8888', '4C13752CE7AE4D187264A8CC7AB38F50D2108B7645B5EDDD8050FC350DBDB7F8', '/county-dashboard', 200, '2024-06-17 17:00:02.980907', 0, 605);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (609, 'countyadmin2', 'localhost:8888', 'ECB46D748E0584493799ADB9370CD43C001CEA0045EDD8E14DDFA40B4E3629E4', '/contest/county', 200, '2024-06-17 17:00:02.996896', 0, 608);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (610, 'stateadmin1', 'localhost:8888', 'CC1FE6758863764C096B1156D5F1A4C34B511BCFA566466432FF7BD35CE4BE3E', '/dos-dashboard', 200, '2024-06-17 17:00:06.004704', 0, 609);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (611, 'stateadmin1', 'localhost:8888', '41217323FC3B09A0B2B15B539F1569A6FC2C6E98E0A070836EF4E1289D90F4EE', '/contest', 200, '2024-06-17 17:00:06.032815', 0, 610);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (614, 'countyadmin2', 'localhost:8888', '15D3DAC26C4F0BD8B95EC3D37E13664CE9FA3A0F1756404FFB678638DF8EEA77', '/county-dashboard', 200, '2024-06-17 17:00:08.152946', 0, 611);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (615, 'countyadmin2', 'localhost:8888', '2D90D3673541A9114DC2CA030B1087CC5259ADEC3B4400AE0FF689F150E21576', '/contest/county', 200, '2024-06-17 17:00:08.166573', 0, 614);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (616, 'stateadmin1', 'localhost:8888', 'E053A374E08614DB9D00EAB44DA3E6AF9A30DDB26A9BD42FFB3E106B12263674', '/dos-dashboard', 200, '2024-06-17 17:00:11.142406', 0, 615);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (617, 'stateadmin1', 'localhost:8888', '91755F9BD0619D52AC8B00F7C572A294EEC824C58A20BF50682BF257DFA8D377', '/contest', 200, '2024-06-17 17:00:11.169338', 0, 616);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (522, 'countyadmin2', 'localhost:8888', 'C58E263CAF8E9AE19506909F23C46F08EC1A2A9AFDEEFFA0AAA24C66AC46F9A8', '/audit-board-asm-state', 200, '2024-06-17 16:58:52.67034', 0, 521);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (528, 'countyadmin2', 'localhost:8888', '7516E08651713E43015530C203053B4E99DAE909C0B29D35D6BA13F18005CC3C', '/audit-board-asm-state', 200, '2024-06-17 16:58:57.691128', 0, 527);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (534, 'countyadmin2', 'localhost:8888', 'BD5F78D2D9138BAD28F3497E7F0ECB720AFBA4894D62BF875C823CE50648688F', '/audit-board-asm-state', 200, '2024-06-17 16:59:02.712647', 0, 533);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (540, 'countyadmin2', 'localhost:8888', '9D31AD42A34835B963665AC0C7E701B1B3B1131212D41F0DC2E3FFCB9302BF0D', '/audit-board-asm-state', 200, '2024-06-17 16:59:07.734854', 0, 539);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (547, 'countyadmin2', 'localhost:8888', '6C6EE8A95622521C9BC9105ED38936C784AC8BDFC9BE061615EFE35F1B591C86', '/county-asm-state', 200, '2024-06-17 16:59:12.760779', 0, 545);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (553, 'countyadmin2', 'localhost:8888', 'DCEAD9149534D9166A2B7DF5FD59E23C89255FCD7F5B9F94552F876F95392AF4', '/county-asm-state', 200, '2024-06-17 16:59:17.783412', 0, 551);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (558, 'countyadmin2', 'localhost:8888', 'A5346BD5A16308CEC5319D2C7453C63154BD88A5941870A4BB6404EA3060E523', '/audit-board-asm-state', 200, '2024-06-17 16:59:22.808261', 0, 557);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (564, 'countyadmin2', 'localhost:8888', 'F0C13270CE445E36FBF2671DC844D2F6142AE4B9CDE57E6D2DB493EB5C97B58C', '/audit-board-asm-state', 200, '2024-06-17 16:59:27.827616', 0, 563);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (571, 'countyadmin2', 'localhost:8888', '7AD20F3E07BED5090F8F40A654357450209D6AF447F1874A6F41C579B0A33AD2', '/county-asm-state', 200, '2024-06-17 16:59:32.848821', 0, 569);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (576, 'countyadmin2', 'localhost:8888', '4C18B86DC8335529C97C245EAD03E20933E46BC4C57002BD4011726E17EF7DBF', '/county-asm-state', 200, '2024-06-17 16:59:37.869811', 0, 575);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (583, 'countyadmin2', 'localhost:8888', '3B829B5DD218BA72BDAB56F4149185E3DFA9D5564D35A1FB2D2C4AD0160419DE', '/audit-board-asm-state', 200, '2024-06-17 16:59:42.89225', 0, 581);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (590, 'countyadmin2', 'localhost:8888', '0EA2D6F50E3EBEEA11B8F0B8E009ED8849D4CBB0D91C812CBB26AE6C4D7C28D2', '/county-dashboard', 200, '2024-06-17 16:59:47.917597', 0, 587);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (591, 'countyadmin2', 'localhost:8888', '64D66198F8BB1FD90E3CEB0B866AF7C14EB87B4E5AC88BCCC4BE62C4F8D27D35', '/contest/county', 200, '2024-06-17 16:59:47.935425', 0, 590);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (592, 'stateadmin1', 'localhost:8888', '52719C3ED83F9565B50FC0B93B5FCA0348F044B83475C980784F1A0443877646', '/dos-dashboard', 200, '2024-06-17 16:59:50.545251', 0, 591);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (593, 'stateadmin1', 'localhost:8888', '5789E785D85BC7585B5D92B48BC8D2CF963A7408A18E2A0E35035A171DAFEE6E', '/contest', 200, '2024-06-17 16:59:50.570051', 0, 592);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (594, 'countyadmin2', 'localhost:8888', '5D143ACF8E951139681906081F36181F151D9F5CA73308B07CC0A8BEDC1D849B', '/county-asm-state', 200, '2024-06-17 16:59:52.937444', 0, 593);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (600, 'countyadmin2', 'localhost:8888', '987E0FEB71752577B395D0ACDB7A18525CDC5C770E210CDA0C19192D585479B4', '/county-asm-state', 200, '2024-06-17 16:59:57.958465', 0, 599);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (606, 'countyadmin2', 'localhost:8888', '40E51048DE4971A5D8EA520C3F01CB4158504B30216B491D5511ECD79CD1EE83', '/county-asm-state', 200, '2024-06-17 17:00:02.977764', 0, 605);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (613, 'countyadmin2', 'localhost:8888', '137A08ADFD5A9F25F3E43B1CA8FA3476FD3F17A48E2EC8589139817705E3C26F', '/audit-board-asm-state', 200, '2024-06-17 17:00:08.149902', 0, 611);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (618, 'countyadmin2', 'localhost:8888', '3E18E9FB0D93FF6F1D8A034722C681BE68F7730A2DA381932427093E11CF52E3', '/audit-board-asm-state', 200, '2024-06-17 17:00:13.171276', 0, 617);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (625, 'countyadmin2', 'localhost:8888', '5B10513B4A45FD3821A88E818B7F09172345B3934287B5C0AC07C87B9A8079D6', '/county-asm-state', 200, '2024-06-17 17:00:18.195489', 0, 623);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (630, 'countyadmin2', 'localhost:8888', '138BC1CE037A17AB1E354A6682D4E7C4B8937CF6CA412F1DB78F3CC345A80705', '/audit-board-asm-state', 200, '2024-06-17 17:00:23.219497', 0, 629);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (633, 'countyadmin2', 'localhost:8888', 'A7D33F40B5F51A96B439FCDC2A8FB28747AFF1660B21CF404929A9CE952B0C90', '/contest/county', 200, '2024-06-17 17:00:23.239907', 0, 632);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (634, 'stateadmin1', 'localhost:8888', '3372A6FAEA1E807C442F5BEAA39691067EA407110877FC4CA1D39481DFC6C2D4', '/dos-dashboard', 200, '2024-06-17 17:00:26.572654', 0, 633);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (635, 'stateadmin1', 'localhost:8888', '59773C2B5BCB8992336691DE9349CD555619F52488039B7C5BA58962CBD1321F', '/contest', 200, '2024-06-17 17:00:26.604473', 0, 634);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (637, 'countyadmin2', 'localhost:8888', 'EC0DF9368DA92B41D7705F8DBC3F63E1E8C7BD1C1D1FAE649E2317A3420952DA', '/audit-board-asm-state', 200, '2024-06-17 17:00:28.241996', 0, 635);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (524, 'countyadmin2', 'localhost:8888', 'F9985EE1C53936DACEFC31716011DD8A59E2DA32CB799B1CB142D2801FCD5ED5', '/county-dashboard', 200, '2024-06-17 16:58:52.67349', 0, 521);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (525, 'countyadmin2', 'localhost:8888', '99762000020A307AEF975874EC731CFA310EA14DF03B6BD0A4DF887578B8315D', '/contest/county', 200, '2024-06-17 16:58:52.689932', 0, 524);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (526, 'stateadmin1', 'localhost:8888', '08A85E56AB4F650D0F7F57982EB767DBCFEBC2F0341B54F68FAABAE5CD51DBAF', '/dos-dashboard', 200, '2024-06-17 16:58:53.874221', 0, 525);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (527, 'stateadmin1', 'localhost:8888', '09AFDC16E7AC1021D095361A050A6C7ECA77E58E8AC2264A4177FFC60EF201CA', '/contest', 200, '2024-06-17 16:58:53.904126', 0, 526);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (530, 'countyadmin2', 'localhost:8888', '349E6080B0B986700432D6741E39A574D4E1A964A9022AE72DABCE41D3C5D45B', '/county-dashboard', 200, '2024-06-17 16:58:57.694496', 0, 527);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (531, 'countyadmin2', 'localhost:8888', '9D4E28C813503EF07089F47FFC6616DF5F46AA0DCFF1693016BA51CF461E6BD4', '/contest/county', 200, '2024-06-17 16:58:57.710227', 0, 530);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (532, 'stateadmin1', 'localhost:8888', '9770C5594BA14CEFB7AA0E94147695A42BF25D409B0E163CD832745847FBF8EF', '/dos-dashboard', 200, '2024-06-17 16:58:59.039466', 0, 531);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (533, 'stateadmin1', 'localhost:8888', '2AC054A1D940AFD3205CD39DE17DC38567AAF754B8307F86510F16C886AAE40A', '/contest', 200, '2024-06-17 16:58:59.067106', 0, 532);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (536, 'countyadmin2', 'localhost:8888', '52DEFB4BFE9CDC52DBE05EF4409E86701A51D0F956CD97E0814401DF9F501387', '/county-dashboard', 200, '2024-06-17 16:59:02.715581', 0, 533);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (537, 'countyadmin2', 'localhost:8888', 'BD9165C2E81B90C1782A2BD21F93F0B6FC6D6AB27158C5C8D48EA19C277202E6', '/contest/county', 200, '2024-06-17 16:59:02.730726', 0, 536);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (538, 'stateadmin1', 'localhost:8888', '987E55893B3D67C5DA01A07170718DD9F8B3AC45DAEBF6F37D097B8E715936C0', '/dos-dashboard', 200, '2024-06-17 16:59:04.199669', 0, 537);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (539, 'stateadmin1', 'localhost:8888', 'B1ABF9E82CB6AA429054F98535A536533393B9596EDDF70E6CAC7BCE462FD4F6', '/contest', 200, '2024-06-17 16:59:04.227677', 0, 538);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (542, 'countyadmin2', 'localhost:8888', 'E9D193AF59FFD2F7777E2C93B2ABCDA411B50A704D3C8B78ED87280F2074FC6B', '/county-dashboard', 200, '2024-06-17 16:59:07.738026', 0, 539);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (543, 'countyadmin2', 'localhost:8888', 'B78563C127D1B1FB3FF8A1EE64AC9E7E7C975DEB64B6E03CA8467F037C57C3C4', '/contest/county', 200, '2024-06-17 16:59:07.755531', 0, 542);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (544, 'stateadmin1', 'localhost:8888', 'E4032430610D3AD8910DF581853B7CC3A60DD372FBC4CCF093529FEB590163A5', '/dos-dashboard', 200, '2024-06-17 16:59:09.344805', 0, 543);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (545, 'stateadmin1', 'localhost:8888', '6511C7B3A4C446CF3B665CA2D993D57AE547A8F57FCA272158205FE0438FBE35', '/contest', 200, '2024-06-17 16:59:09.377641', 0, 544);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (548, 'countyadmin2', 'localhost:8888', 'ACED5BCC9DAEBB3E2A4CCD84EC60693BA59D7553BCDA920EEB674649B3DD4902', '/county-dashboard', 200, '2024-06-17 16:59:12.76348', 0, 545);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (549, 'countyadmin2', 'localhost:8888', '6F1666DD388293962B3519A90DB99EE5B24C102EAE96A56676D73E2DE2AA615D', '/contest/county', 200, '2024-06-17 16:59:12.780827', 0, 548);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (550, 'stateadmin1', 'localhost:8888', '70E103F75520503190A58FF64EC6BA8098B1FC16A9CE08E25A638AA35E553905', '/dos-dashboard', 200, '2024-06-17 16:59:14.500107', 0, 549);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (551, 'stateadmin1', 'localhost:8888', '08AE8DBFFEA7AC2903E99F5506BB15A9AB7B8FFFCF00150A8A4A0082C02F5150', '/contest', 200, '2024-06-17 16:59:14.531268', 0, 550);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (552, 'countyadmin2', 'localhost:8888', 'E345FDE3A6331E06CD7E5F40BAB67FAB326483FD38C8204B7734D42DE5DAE1A7', '/audit-board-asm-state', 200, '2024-06-17 16:59:17.783416', 0, 551);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (559, 'countyadmin2', 'localhost:8888', 'B0AC1F2AC5BB78722DB57A92D0A241EE88A0DA4501E96814DB641A25035ED55F', '/county-asm-state', 200, '2024-06-17 16:59:22.808261', 0, 557);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (565, 'countyadmin2', 'localhost:8888', '877271866220318E6AF8515374E02664F696C7F31264A01A9D7C1273613C2C0E', '/county-asm-state', 200, '2024-06-17 16:59:27.827616', 0, 563);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (570, 'countyadmin2', 'localhost:8888', '2EFBF2246CE4712D6DBEDBCD32DCFEF874A3253338E3A2F5C0ECC7ABAF67F9D0', '/audit-board-asm-state', 200, '2024-06-17 16:59:32.848786', 0, 569);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (577, 'countyadmin2', 'localhost:8888', '2347EF869EDE082263DCCAB721A79B07EB0DEFFD5AB445640CD3BDD0F5D37307', '/audit-board-asm-state', 200, '2024-06-17 16:59:37.869909', 0, 575);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (582, 'countyadmin2', 'localhost:8888', 'EB86844F601CFF609F026A136A4940CBC11FD9E040D6419453D012DEBE43E438', '/county-asm-state', 200, '2024-06-17 16:59:42.892171', 0, 581);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (589, 'countyadmin2', 'localhost:8888', 'B9777FC4B465ABCF6C4EB69518EFCC5AF5532E837C1755C433EB5705906E449E', '/audit-board-asm-state', 200, '2024-06-17 16:59:47.913221', 0, 587);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (595, 'countyadmin2', 'localhost:8888', '2CBE6F8BBF2645A0F55E4072C7A95D81C8E1A671A576F8277E428258F06F5990', '/audit-board-asm-state', 200, '2024-06-17 16:59:52.937618', 0, 593);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (601, 'countyadmin2', 'localhost:8888', 'ACB907F6199C8E6F331BCC67A1CB8A501DCFFE2DE9E06430B3B10CEFAC1C066E', '/audit-board-asm-state', 200, '2024-06-17 16:59:57.958425', 0, 599);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (607, 'countyadmin2', 'localhost:8888', 'B73E958DBBE2AA3B61B9F1722BC8C95481342D247D049A1CF0793779722AED73', '/audit-board-asm-state', 200, '2024-06-17 17:00:02.977911', 0, 605);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (612, 'countyadmin2', 'localhost:8888', 'CDEB7D27DFEB4D7467905DE1155D9C7D7D4268F7048D2549E8A6DC1C417F8A95', '/county-asm-state', 200, '2024-06-17 17:00:08.149698', 0, 611);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (619, 'countyadmin2', 'localhost:8888', '58982056F501E1CCBC74D18453664677B8DB4142EC3DD53D4877E2035FAE9042', '/county-asm-state', 200, '2024-06-17 17:00:13.171466', 0, 617);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (620, 'countyadmin2', 'localhost:8888', '0E684EB32697C70F61FEDAC22EB6C590782DAD99A2F08C11E0782A2468FD16FA', '/county-dashboard', 200, '2024-06-17 17:00:13.174709', 0, 617);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (621, 'countyadmin2', 'localhost:8888', '178B4902C2A9FD7B4E42B5EC9F7B8F1BC2080E408E347E626C0407C9F1165364', '/contest/county', 200, '2024-06-17 17:00:13.192594', 0, 620);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (622, 'stateadmin1', 'localhost:8888', 'B66BD8B07DF624E21B1D50E71AB8FDCE0FF020864819CA265900AD9B22E598E6', '/dos-dashboard', 200, '2024-06-17 17:00:16.284061', 0, 621);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (623, 'stateadmin1', 'localhost:8888', 'A96CC58C1A8F27FC5BC66F672C1473FEAB6981EAF68F4248D010FE1A3A803959', '/contest', 200, '2024-06-17 17:00:16.31545', 0, 622);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (624, 'countyadmin2', 'localhost:8888', '39E9B94334487D77A6D202A37DA6FDE90C7E07AF4E2E1F07D21D5FDFEB5A2247', '/audit-board-asm-state', 200, '2024-06-17 17:00:18.195008', 0, 623);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (626, 'countyadmin2', 'localhost:8888', 'F7292566D948DCEFA28FBF70F6E24992E0C27BB3D535AA389906BC8C169DC94A', '/county-dashboard', 200, '2024-06-17 17:00:18.197923', 0, 623);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (627, 'countyadmin2', 'localhost:8888', 'DF447AD68D9C214F8E1FA73B020E62CF938110C13374658ECBE65C96ADA35877', '/contest/county', 200, '2024-06-17 17:00:18.216935', 0, 626);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (628, 'stateadmin1', 'localhost:8888', 'D95DC642C904CA6DA310A98F835891B6A1E991BF4FD715A8BCBE58C0705F58CD', '/dos-dashboard', 200, '2024-06-17 17:00:21.430046', 0, 627);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (629, 'stateadmin1', 'localhost:8888', '33A4CB154F0E84E6D6FA3F16372D82D30AF39A1CF4919410B204235A92DFCCC7', '/contest', 200, '2024-06-17 17:00:21.485309', 0, 628);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (631, 'countyadmin2', 'localhost:8888', '3BBA4CEDAD2DB0170E20540B3F7ABD4D2E530772A3F96757406A91A08FCCDEFD', '/county-asm-state', 200, '2024-06-17 17:00:23.219538', 0, 629);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (632, 'countyadmin2', 'localhost:8888', 'FA7D978F5CDE7E087FB357541DC59EEF78F67EA65B54FBE787480BD3C5BB9447', '/county-dashboard', 200, '2024-06-17 17:00:23.222318', 0, 629);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (636, 'countyadmin2', 'localhost:8888', '177B578CDDF5273C76B0DAE2059E3008A280B621F82395D3394FD59B1F1C1B52', '/county-asm-state', 200, '2024-06-17 17:00:28.242096', 0, 635);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (638, 'countyadmin2', 'localhost:8888', 'DED36807C0BFC2C7528403CF56951A02EE7716E9A5640342791535413292171C', '/county-dashboard', 200, '2024-06-17 17:00:28.244432', 0, 635);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (639, 'countyadmin2', 'localhost:8888', '51BB7D1A6125383C7CE6049FC19A78BEEFFD5992D188734D6CFFE02C09933045', '/contest/county', 200, '2024-06-17 17:00:28.262556', 0, 638);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (640, 'stateadmin1', 'localhost:8888', '79B37BA9C4A6BE957C90C94B16FB610A9C0F01C665D0E4E064F0F97B1FCCB4C4', '/dos-dashboard', 200, '2024-06-17 17:00:31.716171', 0, 639);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (641, 'stateadmin1', 'localhost:8888', '3703EAC3BD8B3FE80A8A310AD5F0FA5BD148A2CC972F0281A073922DBD678C2E', '/contest', 200, '2024-06-17 17:00:31.744374', 0, 640);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (642, 'countyadmin2', 'localhost:8888', '05102047E4E4F06C5C5C9DC162F000A73CEFA02FA5B0573545F1F308DB0C58D6', '/audit-board-asm-state', 200, '2024-06-17 17:00:33.265733', 0, 641);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (643, 'countyadmin2', 'localhost:8888', '13B03170C31590F1F95663B37B85BADA542C355A44F968B1800CAA65E964C317', '/county-asm-state', 200, '2024-06-17 17:00:33.266105', 0, 641);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (644, 'countyadmin2', 'localhost:8888', 'A16490F0611955D6F67EBC5374B9568A3119D0D98F6062DAD583AA5CFDC14024', '/county-dashboard', 200, '2024-06-17 17:00:33.269396', 0, 641);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (645, 'countyadmin2', 'localhost:8888', '5359F54CA33189F3BE3D3496D3BA812F0F4DF5368FB68CCA91E1ECE7F676AD60', '/contest/county', 200, '2024-06-17 17:00:33.2844', 0, 644);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (646, 'stateadmin1', 'localhost:8888', 'EA9D5065A181D649326B27FDAF409CEC1BE75BD9620D735E18048C6F583BB8CF', '/dos-dashboard', 200, '2024-06-17 17:00:36.869306', 0, 645);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (647, 'stateadmin1', 'localhost:8888', 'BF087FAA369B3DB8787DB0CFC6DAA329F743691A8563E9B3440F842EB2F643A2', '/contest', 200, '2024-06-17 17:00:36.898282', 0, 646);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (649, 'countyadmin2', 'localhost:8888', '3E21AA78F09394C0056C1A24480EABED17EB5DF6A1320EDBBA14321B219DF7A5', '/audit-board-asm-state', 200, '2024-06-17 17:00:38.286601', 0, 647);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (648, 'countyadmin2', 'localhost:8888', '7A885ACC35A1F79B8D0E2770CAE4782FFA80F815B52141EA53CA6D2FED356805', '/county-asm-state', 200, '2024-06-17 17:00:38.286513', 0, 647);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (650, 'countyadmin2', 'localhost:8888', 'B452AF7F3D614A08FB6767BF732CCE2FE9D8DDE75D2443E564DD42FC984FDB54', '/county-dashboard', 200, '2024-06-17 17:00:38.289822', 0, 647);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (651, 'countyadmin2', 'localhost:8888', '350FFE85CDBF2FBBC3A940CBBB4F9318FF4D11B5CCC731776237D099B140DDBC', '/contest/county', 200, '2024-06-17 17:00:38.30678', 0, 650);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (652, 'stateadmin1', 'localhost:8888', '4157D8AE936BD9B4323AEFCDE6BF3C0F3DDE3CBACBC5BB7BD06CCB5C2753ED53', '/dos-dashboard', 200, '2024-06-17 17:00:41.999895', 0, 651);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (653, 'stateadmin1', 'localhost:8888', 'FCD18CCD2A04EC1CEEEF1ACA018BF3A70D7587A194F7A101058A84C465B4E608', '/contest', 200, '2024-06-17 17:00:42.021374', 0, 652);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (654, 'countyadmin2', 'localhost:8888', 'B2858DA983AAA667DE08BB444D0ED1B7871332DDCCF7AA6A797FAD398F9EE312', '/county-asm-state', 200, '2024-06-17 17:00:43.308645', 0, 653);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (655, 'countyadmin2', 'localhost:8888', 'FE69EEFA19C58D2FA357B88736727713605F73E8ECA9137B1393FE17D99DEF20', '/audit-board-asm-state', 200, '2024-06-17 17:00:43.30943', 0, 653);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (656, 'countyadmin2', 'localhost:8888', 'C61CAD8D21767D7B8AE3266BF8F337618CC4ED2E4AA50E287BC25E58291FF28C', '/county-dashboard', 200, '2024-06-17 17:00:43.316497', 0, 653);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (657, 'countyadmin2', 'localhost:8888', 'CE1C7FE4414C2B94DB72ACA1D9C5DBACC6DFDAAA00A36982626D1B3250A2B8CE', '/contest/county', 200, '2024-06-17 17:00:43.330067', 0, 656);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (658, 'stateadmin1', 'localhost:8888', '9E0E85B94FDBA1C87281B43A67D9C93DF8944DF0758FBC767978BD50DD167BD6', '/dos-dashboard', 200, '2024-06-17 17:00:47.146615', 0, 657);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (659, 'stateadmin1', 'localhost:8888', 'A1AB6D98A879CC89B1F1A621D2C143C64C7B4934017D541FF2730D28B359E07E', '/contest', 200, '2024-06-17 17:00:47.176755', 0, 658);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (661, 'countyadmin2', 'localhost:8888', 'A556187985C5B2B70F2C9598169D2BB506CB3F8D663B3D4FBC2B670150F2B4C6', '/audit-board-asm-state', 200, '2024-06-17 17:00:48.331395', 0, 659);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (660, 'countyadmin2', 'localhost:8888', 'E4D27FDD2A21F038437708E9E67659AD41BD48D836EF37853D9141B56CA33E50', '/county-asm-state', 200, '2024-06-17 17:00:48.331266', 0, 659);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (662, 'countyadmin2', 'localhost:8888', '6047A88E79DE1CE27762DF1AF4ABE7B9839B2E325FD4BD2378E5EBDF3BBEFAE7', '/county-dashboard', 200, '2024-06-17 17:00:48.335638', 0, 659);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (663, 'countyadmin2', 'localhost:8888', 'F8EBF13B140851356A05C9991F79D54DD0627049DB9203A962D16DA4EAD4F845', '/contest/county', 200, '2024-06-17 17:00:48.351766', 0, 662);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (664, 'stateadmin1', 'localhost:8888', '240D5F8FC47D8375E22FD51764DAAE9FB862EBE0BD9E6C54222213A772042794', '/dos-dashboard', 200, '2024-06-17 17:00:52.294366', 0, 663);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (665, 'stateadmin1', 'localhost:8888', '0F9D99A40E4C78F7A2F837F7B688052037DF204B6B13B9EBD77DB03F8B606C1B', '/contest', 200, '2024-06-17 17:00:52.323591', 0, 664);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (666, 'countyadmin2', 'localhost:8888', '33ED870945FF35BFF429131F25F5FF214FA0B93601BDE5BB5774DF100A66D59C', '/audit-board-asm-state', 200, '2024-06-17 17:00:53.354071', 0, 665);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (667, 'countyadmin2', 'localhost:8888', 'BECC9AB60732176C39EFEEDCAE61D602745391C6FAABD890B64DDDBC42FA5213', '/county-asm-state', 200, '2024-06-17 17:00:53.354208', 0, 665);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (668, 'countyadmin2', 'localhost:8888', 'E0787747574239B5E0B639A38AAC37466D49E51FDF7BD150B7D91C7960ED88B2', '/county-dashboard', 200, '2024-06-17 17:00:53.357449', 0, 665);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (669, 'countyadmin2', 'localhost:8888', '7CE20EDD9B2DDB29CBABB1561EDCD8E9A1D11B5B008DE096E881B07C00393881', '/contest/county', 200, '2024-06-17 17:00:53.373425', 0, 668);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (670, 'stateadmin1', 'localhost:8888', 'ED1AF77C263A8587B88E2C636F76A482626A5FFD2BF8E4E7DB6BDE688B6888B0', '/dos-dashboard', 200, '2024-06-17 17:00:57.409817', 0, 669);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (671, 'stateadmin1', 'localhost:8888', 'CE444A41850E86BCCDA0D1402D5A4B3D728CF3F4D165C680B73D371B05541564', '/contest', 200, '2024-06-17 17:00:57.436567', 0, 670);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (672, 'countyadmin2', 'localhost:8888', 'F5E62244BAA6A3EFCD3B663E936199F6F3B200700C338D7F2AC441A18A8FC996', '/audit-board-asm-state', 200, '2024-06-17 17:00:58.375323', 0, 671);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (673, 'countyadmin2', 'localhost:8888', '224E67A1D9BAEF093DC90BE0A0161DE1E8665074B7C9A1F42A5F3EA2154CE46D', '/county-asm-state', 200, '2024-06-17 17:00:58.375572', 0, 671);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (674, 'countyadmin2', 'localhost:8888', '957435F983DAE50B77C52FEC8DB9D9897D33A62C9B3715CED232FF1898CFC270', '/county-dashboard', 200, '2024-06-17 17:00:58.379171', 0, 671);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (675, 'countyadmin2', 'localhost:8888', '48F7888236A6FB2AE982537C623918E17595A2434FCD0FB1D8595DB2B8B7F70D', '/contest/county', 200, '2024-06-17 17:00:58.392941', 0, 674);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (676, 'stateadmin1', 'localhost:8888', '0BAA3CAC7A7B3C692E41350C9D01B1B5E05370E618680B1C4B63744C6EFDA4E0', '/dos-dashboard', 200, '2024-06-17 17:01:02.549932', 0, 675);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (677, 'stateadmin1', 'localhost:8888', '13898386F917DE5B76803A36D5C33B271F22B1321549EB6D16AAC7D9BA1BB184', '/contest', 200, '2024-06-17 17:01:02.57787', 0, 676);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (678, 'countyadmin2', 'localhost:8888', '90080FCBA239A7AA67EE48EC285CDE014C0165E73314434D2D6FE3C636A4C520', '/county-asm-state', 200, '2024-06-17 17:01:03.457927', 0, 677);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (679, 'countyadmin2', 'localhost:8888', 'D5ED0E1EA791FC3A81A6D61FEB9AD8DBD4F098D5406EDF80BCD91FF9D4048453', '/audit-board-asm-state', 200, '2024-06-17 17:01:03.457981', 0, 677);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (680, 'countyadmin2', 'localhost:8888', '38473F8C6AC15F12F799BA902724E4978C0EE3B6C5EA08A7B0ACB0045F3E1241', '/county-dashboard', 200, '2024-06-17 17:01:03.461259', 0, 677);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (681, 'countyadmin2', 'localhost:8888', 'F992EF7A27148B8054CF58B1573ECF7C402DEBE498C9F2D43484D2399D61A594', '/contest/county', 200, '2024-06-17 17:01:03.476276', 0, 680);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (682, 'stateadmin1', 'localhost:8888', '3706C952D0072AF350DB83AC2EAC6D363D2032763D85047853D60403DF862204', '/dos-dashboard', 200, '2024-06-17 17:01:07.689195', 0, 681);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (683, 'stateadmin1', 'localhost:8888', '985956E677D0B429655386F6B252CCFF4E7A110A151AAF8FED7FD9A3DB9C4D7F', '/contest', 200, '2024-06-17 17:01:07.72092', 0, 682);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (684, 'countyadmin2', 'localhost:8888', '13F4D0A21D65F8071A91EA8D546BAC350387B45A3766F26682285D98CD6A2EA4', '/county-asm-state', 200, '2024-06-17 17:01:08.477722', 0, 683);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (685, 'countyadmin2', 'localhost:8888', 'FEDD6F0DDF86A7334C3F4AC003F8F9184B7FFAD6AFA2ED8C139C4B23C13D24BC', '/audit-board-asm-state', 200, '2024-06-17 17:01:08.477883', 0, 683);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (686, 'countyadmin2', 'localhost:8888', '4D643B211AB016758A3E474A9DE8BD616E650AA16534CBE2CA34106D0F8CF6B3', '/county-dashboard', 200, '2024-06-17 17:01:08.480814', 0, 683);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (687, 'countyadmin2', 'localhost:8888', '9011C4C5FC3D7D5D630AA0DC3C932AD2F32CAC013C07551A4036419AAF4CC2E9', '/contest/county', 200, '2024-06-17 17:01:08.495438', 0, 686);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (688, 'stateadmin1', 'localhost:8888', '716684AA69647772D9C47A91B1F40A7328A512A9E5455013090EC87F4D790518', '/dos-dashboard', 200, '2024-06-17 17:01:12.832934', 0, 687);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (689, 'stateadmin1', 'localhost:8888', '19AFAC4E717D7DE6771EAC8F7F9A5DE433795055AEC948CF566E4C677C4FD694', '/contest', 200, '2024-06-17 17:01:12.861544', 0, 688);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (690, 'countyadmin2', 'localhost:8888', '9907A97D8327EE6BB1089D67E04FE23580F1A523F4C97B444F00B7220149DAF6', '/county-asm-state', 200, '2024-06-17 17:01:13.49758', 0, 689);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (697, 'countyadmin2', 'localhost:8888', '8054BC30A3DA02B3D4138457CA2D03EC51D5C39E296D1836CA9EC8DB8C911009', '/county-asm-state', 200, '2024-06-17 17:01:18.518973', 0, 695);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (702, 'countyadmin2', 'localhost:8888', 'FB3154A5E6683484952755CF84F81EE1FE57DCE043DFDCD41484D3CD55513D7F', '/audit-board-asm-state', 200, '2024-06-17 17:01:23.540626', 0, 701);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (691, 'countyadmin2', 'localhost:8888', 'EA3E12565D9B360ED5CA986F15F76070853738F97D9209EC2E514C092AE9A315', '/audit-board-asm-state', 200, '2024-06-17 17:01:13.497625', 0, 689);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (698, 'countyadmin2', 'localhost:8888', 'A4B3ECCAFE401EA05240FD1510D9B82466AC2363EB3F6635B00964578634A070', '/county-dashboard', 200, '2024-06-17 17:01:18.525798', 0, 695);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (699, 'countyadmin2', 'localhost:8888', '6AF16205A7DF6C363CA6268E49C39C087B15F9243664F1499878384742F79693', '/contest/county', 200, '2024-06-17 17:01:18.539507', 0, 698);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (700, 'stateadmin1', 'localhost:8888', '030DD37018B93ABB52024E004D4A37AA1987E47C59F502A5E70F94EDEEBA7BCF', '/dos-dashboard', 200, '2024-06-17 17:01:23.151536', 0, 699);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (701, 'stateadmin1', 'localhost:8888', 'D987F063662AF255CE8D858D35E68C8B790881515D249A604542F1D8C0F343DF', '/contest', 200, '2024-06-17 17:01:23.185464', 0, 700);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (704, 'countyadmin2', 'localhost:8888', '6B3F4DFA099AF1DC5DDEFF550FD7AAAD5F77167A03902A21DAFCD8DAF1F59035', '/county-dashboard', 200, '2024-06-17 17:01:23.543981', 0, 701);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (705, 'countyadmin2', 'localhost:8888', '853699A881CB6B1BC111607040E9CE4E9B9E8AB82457A14955404F99F73D4D57', '/contest/county', 200, '2024-06-17 17:01:23.559702', 0, 704);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (692, 'countyadmin2', 'localhost:8888', 'F8972ABF81EF406FB0ACB79B897A80531A88A605592131909ABFF4AA70D4B444', '/county-dashboard', 200, '2024-06-17 17:01:13.500932', 0, 689);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (693, 'countyadmin2', 'localhost:8888', '49C2E5F3A36573F08F961335F0AE0A4D5F5E35D9C68DD9636CA00FB4B7E47B59', '/contest/county', 200, '2024-06-17 17:01:13.516011', 0, 692);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (694, 'stateadmin1', 'localhost:8888', 'A307360CF5FCDC8AF607BBBB8192CB34F2B717D641AEFEC5F8E5FA83D37D008C', '/dos-dashboard', 200, '2024-06-17 17:01:17.975963', 0, 693);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (695, 'stateadmin1', 'localhost:8888', '8E3B8D2B0D1B69BA800E8BEAEE352D057E802F70D1693ADE8B761A218EF68BE5', '/contest', 200, '2024-06-17 17:01:18.025391', 0, 694);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (696, 'countyadmin2', 'localhost:8888', '0FF60D06F51A605AB1959897AD4B9FA47623085A2ACD9666546A872CEFDCF53B', '/audit-board-asm-state', 200, '2024-06-17 17:01:18.51894', 0, 695);
INSERT INTO public.log (id, authentication_data, client_host, hash, information, result_code, "timestamp", version, previous_entry) VALUES (703, 'countyadmin2', 'localhost:8888', '452D0B0C12F0EC4D7A521303B9EAC5CB8A362A6CF56EEB631170A68F3C0D7ABF', '/county-asm-state', 200, '2024-06-17 17:01:23.540818', 0, 701);


--
-- Data for Name: round; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: tribute; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--



--
-- Data for Name: uploaded_file; Type: TABLE DATA; Schema: public; Owner: corlaadmin
--

INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (160, '7760C6690CAACD2568CA80ED10B8D3362D561D4573890DA867B2CAC10EE89659', 14, 21548, 'ThreeCandidatesTenVotesPlusPlurality.csv', 1718, '2024-06-17 16:55:47.818924', 1, '{"success":true,"importedCount":10}', 'IMPORTED', '7760C6690CAACD2568CA80ED10B8D3362D561D4573890DA867B2CAC10EE89659', 3);
INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (199, '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 2, 21549, 'ThreeCandidatesTenVotes_Manifest.csv', 78, '2024-06-17 16:56:04.173591', 1, '{"success":true,"importedCount":1,"errorMessage":null,"errorRowNum":null,"errorRowContent":null}', 'IMPORTED', '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 3);
INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (230, 'D2EE3F29D1CCB8B0F4B790493EDC2F41B4081DCC1D760277ACA27268140D166C', 14, 21551, 'ThreeCandidatesTenVotes.csv', 1364, '2024-06-17 16:56:30.354526', 1, '{"success":true,"importedCount":10}', 'IMPORTED', 'D2EE3F29D1CCB8B0F4B790493EDC2F41B4081DCC1D760277ACA27268140D166C', 2);
INSERT INTO public.uploaded_file (id, computed_hash, approximate_record_count, file, filename, size, "timestamp", version, result, status, submitted_hash, county_id) VALUES (261, '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 2, 21552, 'ThreeCandidatesTenVotes_Manifest.csv', 78, '2024-06-17 16:56:41.026971', 1, '{"success":true,"importedCount":1,"errorMessage":null,"errorRowNum":null,"errorRowContent":null}', 'IMPORTED', '5D602A0A0B2BC51A2E9F1F6EFD8650FDD748E4FDD8268104A43A633588A998C3', 2);


--
-- Name: assertion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: corlaadmin
--

SELECT pg_catalog.setval('public.assertion_id_seq', 1, false);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: corlaadmin
--

SELECT pg_catalog.setval('public.hibernate_sequence', 705, true);


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
-- Name: assertion_discrepancies assertion_discrepancies_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.assertion_discrepancies
    ADD CONSTRAINT assertion_discrepancies_pkey PRIMARY KEY (id, cvr_id);


--
-- Name: assertion assertion_pkey; Type: CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.assertion
    ADD CONSTRAINT assertion_pkey PRIMARY KEY (id);


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
-- Name: assertion_context fki0lyp4tghtpohaa9ma6kv2174; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.assertion_context
    ADD CONSTRAINT fki0lyp4tghtpohaa9ma6kv2174 FOREIGN KEY (id) REFERENCES public.assertion(id);


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
-- Name: assertion_discrepancies fkt31yi3mf6c9axmt1gn1mu33ea; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.assertion_discrepancies
    ADD CONSTRAINT fkt31yi3mf6c9axmt1gn1mu33ea FOREIGN KEY (id) REFERENCES public.assertion(id);


--
-- Name: contest_comparison_audit_disagreement fkt490by57jb58ubropwn7kmadi; Type: FK CONSTRAINT; Schema: public; Owner: corlaadmin
--

ALTER TABLE ONLY public.contest_comparison_audit_disagreement
    ADD CONSTRAINT fkt490by57jb58ubropwn7kmadi FOREIGN KEY (contest_comparison_audit_id) REFERENCES public.comparison_audit(id);


--
-- PostgreSQL database dump complete
--

