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
-- PostgreSQL database dump complete
--

