lexer grammar FateLexer;

@header
{
   package tonkadur.fate.v1.parser;
}

fragment SEP: [ \t\r\n]+;

WS: SEP;

L_PAREN: '(';
R_PAREN: ')';

ADD_KW: L_PAREN 'add';
ADD_VARIABLE_ATTRIBUTE_KW: L_PAREN 'add_variable_attribute';
AND_KW: L_PAREN ('and'|'/\\');
ASSERT_KW: L_PAREN 'assert';
CAST_KW: L_PAREN 'cast';
CLEAR_KW: L_PAREN 'clear';
COND_KW: L_PAREN 'cond';
COUNT_KW: L_PAREN 'count';
DECLARE_ALIAS_TYPE_KW: L_PAREN 'declare_subtype';
DECLARE_DICT_TYPE_KW: L_PAREN 'declare_dict_type';
DECLARE_EVENT_TYPE_KW: L_PAREN 'declare_event_type';
DECLARE_LIST_TYPE_KW: L_PAREN 'declare_list_type';
DECLARE_REF_TYPE_KW: L_PAREN 'declare_ref_type';
DECLARE_SET_TYPE_KW: L_PAREN 'declare_set_type';
DECLARE_TEXT_EFFECT_KW: L_PAREN 'declare_text_effect';
DECLARE_VARIABLE_KW: L_PAREN 'declare_variable';
DEFINE_MACRO_KW: L_PAREN 'define_macro';
DEFINE_SEQUENCE_KW: L_PAREN 'define_sequence';
DIVIDE_KW: L_PAREN ('divide'|'/');
ENABLE_TEXT_PARAMETER_KW: L_PAREN 'enable_text_parameter';
EQUALS_KW: L_PAREN ('equals'|'='|'==');
EVENT_KW: L_PAREN 'event';
EXTENSION_FIRST_LEVEL_KW: L_PAREN '@';
EXTENSION_INSTRUCTION_KW: L_PAREN '#';
EXTENSION_VALUE_KW: L_PAREN '$';
FALSE_KW: L_PAREN 'false)';
FATE_VERSION_KW: L_PAREN 'fate_version';
GREATER_EQUAL_THAN_KW: L_PAREN ('greater_equal_than'|'>=');
GREATER_THAN_KW: L_PAREN ('greater_than'|'>');
IF_ELSE_KW: L_PAREN 'if_else';
IF_KW: L_PAREN 'if';
IMPLIES_KW: L_PAREN ('implies'|'=>');
INCLUDE_KW: L_PAREN 'include';
IS_MEMBER_KW: L_PAREN 'is_member';
LOWER_EQUAL_THAN_KW: L_PAREN ('lower_equal_than'|'=<'|'<=');
LOWER_THAN_KW: L_PAREN ('lower_than'|'<');
MACRO_KW: L_PAREN 'macro';
MINUS_KW: L_PAREN ('minus'|'-');
NEWLINE_KW: L_PAREN 'newline)';
NOT_KW: L_PAREN ('not'|'~'|'!');
ONE_IN_KW: L_PAREN 'one_in';
OR_KW: L_PAREN ('or'|'\\/');
PARAMETER_KW: L_PAREN ('param'|'parameter');
PLAYER_CHOICE_KW: L_PAREN ('choice'|'user_choice'|'player_choice');
PLUS_KW: L_PAREN ('plus'|'+');
POWER_KW: L_PAREN ('power'|'^'|'**');
RANDOM_KW: L_PAREN ('random'|'rand');
REF_KW: L_PAREN 'ref';
REMOVE_ALL_KW: L_PAREN 'remove_all';
REMOVE_ONE_KW: L_PAREN 'remove_one';
REQUIRE_EXTENSION_KW: L_PAREN 'require_extension';
REQUIRE_KW: L_PAREN 'require';
SEQUENCE_KW: L_PAREN 'sequence';
SET_EXPRESSION_KW: L_PAREN 'set_expression';
SET_FIELDS_KW: L_PAREN 'set_fields';
SET_KW: L_PAREN 'set';
TIMES_KW: L_PAREN ('times'|'*');
TRUE_KW: L_PAREN 'true)';
VAL_KW: L_PAREN 'val';
VARIABLE_KW: L_PAREN 'variable';

WORD: (~([ \t\r\n()])|'\\)'|'\\(')+;

COMMENT: WS* ';' .*? '\n' -> channel(HIDDEN);
