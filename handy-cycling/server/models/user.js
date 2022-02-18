module.exports = (sequelize, DataTypes) => {
  return sequelize.define(
    'user',
    {
      user_id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false,
      },

      name: {
        type: DataTypes.STRING(20),
        allowNull: false,
        defaultValue: 0,
      },

      id: {
        type: DataTypes.STRING(30),
        allowNull: false,
      },

      pw: {
        type: DataTypes.STRING(50),
        allowNull: false,
      },

      point: {
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 0,
      },

      signup_date: {
        type: DataTypes.DATE,
        allowNull: false,
      }
    },
    {
      charset: 'utf8',
      collate: 'utf8_general_ci',
      timestamps: false,
    }
  )
};