export default {


    filters: {
        
        incialMaiuscula: function (value){
            if (!value) return ''
            value = value.toString()
            return value.charAt(0).toUpperCase() + value.slice(1)
        },
        
        todasMaiusculas: function(value){
            if (!value) return ''
            value = value.toString()
            return value.toUpperCase()
        },

        formatarCpfCnpj: function(documentId) {
            documentId = typeof documentId != 'string' ? documentId.toString() : documentId;
           
            if (documentId.length > 11 ) {
              documentId = documentId.padStart(14, '0');
              documentId = documentId.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
            } else {
              documentId = documentId.padStart(11, '0');
              documentId = documentId.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
            }
            return documentId;
        },


        formatarDataEntrada: function (value) {
            if (value === null) { return null }
            return new Date(value).toLocaleString('pt-BR', { year: 'numeric', month: '2-digit', day: '2-digit' })
          },

        numberToReal: function(value) {
            let val = (value/1).toFixed(2).replace('.', ',')
            return val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".")
          }
    }

}