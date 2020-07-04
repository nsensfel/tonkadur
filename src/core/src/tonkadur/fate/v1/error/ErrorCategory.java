package tonkadur.fate.v1.error;

class ErrorCategory extends tonkadur.error.ErrorCategory
{
   public static final ErrorCategory DUPLICATE_DECLARATION;
   public static final ErrorCategory CONFLICTING_DECLARATION;
   public static final ErrorCategory MISSING_DECLARATION;
   public static final ErrorCategory UNKNOWN_SEQUENCE;
   public static final ErrorCategory INCORRECT_TYPE;
   public static final ErrorCategory INCOMPATIBLE_TYPE;

   static
   {
      DUPLICATE_DECLARATION = new ErrorCategory("duplicate_declaration");
      CONFLICTING_DECLARATION = new ErrorCategory("conflicting_declaration");
      MISSING_DECLARATION = new ErrorCategory("missing_declaration");
      UNKNOWN_SEQUENCE = new ErrorCategory("unknown_sequence");
      INCORRECT_TYPE = new ErrorCategory("incorrect_type");
      INCOMPATIBLE_TYPE = new ErrorCategory("incompatible_type");
   }

   private ErrorCategory (final String name)
   {
      super(name);
   }
}
